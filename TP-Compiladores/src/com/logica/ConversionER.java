package com.logica;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.annotation.processing.SupportedSourceVersion;

public class ConversionER {
	/* Atributos */
	public ArrayList<State> statesList = new ArrayList<State>();
	public String expression;
	
	

	public static void main(String[] args) {
		ConversionER conversion = new ConversionER();
		ConversionER aux = new ConversionER();

		State nextState = new State();
		State newState = new State();
		Read reader = new Read();

		/* Teste de entrada via teclado */
		Scanner in = new Scanner(System.in);
		String expression = in.nextLine();
		in.close();
		/* Fim Teste de entrada via teclado */

		/* Talvez dê pala com mais parênteses */
		conversion.statesList = new ArrayList<State>();
		conversion = aux.logicFunction(aux, expression);
		conversion.statesList = conversion.setInitialOrEnd(conversion.statesList);

		System.out.println();
		for (int i = 0; i < conversion.statesList.size(); i++) {
			if (conversion.statesList.get(i).isChainStart()) {
				System.out.println("ESSE EMBAIXO E CHAIN START!");
			} if (conversion.statesList.get(i).isChainEnd()) {
				System.out.println("ESSE EMBAIXO E CHAIN END!");
			}  if (conversion.statesList.get(i).isUnionStart()) {
				System.out.println("ESSE EMBAIXO E UNION START!");
			}  if (conversion.statesList.get(i).isUnionEnd()) {
				System.out.println("ESSE EMBAIXO E UNION END!");
			} 	if (conversion.statesList.get(i).isInsideUnion()) {
				System.out.println("ESSE EMBAIXO E INSIDE UNION!");
			} 
			conversion.statesList.get(i).printState();
		}
		
		conversion.writeGraphviz(conversion);
	}

	public ConversionER logicFunction(ConversionER mainList, String expression) {
		State logicState = new State();
		State newState = new State();
		Read reader = new Read();

		for (int i = 0; i < expression.length(); i++) {
			/****************************************************** REFATORAÇÃO TOTAL ******************************************************/

			if (reader.isLetterLower(expression.charAt(i))) {
				/****************************************************** PASSO1 - 'a' GERA 2 ESTADOS ******************************************************/
				if (mainList.statesList.size() == 0) {
					/* Crio o estado inicial */
					newState = new State(0,
							Character.toString(expression.charAt(i)), 
							new State((1))
							);
					newState.setChainStart(true);
					mainList.statesList.add(newState);

					/* Crio a ligação e o estado final */
					newState = new State(1,
							"F", 
							new State((-1))
							);
					newState.setChainEnd(true);
					mainList.statesList.add(newState);
				}
				
				else if(mainList.statesList.size() > 0) {
					/* Encontro os ChainStart e ChainEnd para a lógica */
					int positionStart = logicState.findChainStart(mainList.statesList);
					int positionEnd = logicState.findChainEnd(mainList.statesList);
					
					/**************************** PASSO 6.1 da União - Segunda perna! ****************************/
					/**************************** Adiciona o segundo no tutz tutz!  ****************************/
					if(mainList.statesList.get(mainList.getLastState()).isUnionEnd()) {
						
						/* Uso o ChainStart*/
						/* Crio um novo estado - que será linkado pelo primeiro */
						newState = new State(mainList.statesList.size(),
											Character.toString(expression.charAt(i)), 
											new State(mainList.statesList.size()+1)
											);
						mainList.statesList.add(newState);
						
						/* Linko o UnionStart no novo estado */
						mainList.statesList.get(positionStart).addNextState(new State(mainList.statesList.size()-1));
						mainList.statesList.get(positionStart).addItemtoChange("L");
						
						/* Crio o novo estado final já linkado no union end */
						newState = new State(mainList.statesList.size(),
											"L", 
											new State(mainList.statesList.get(positionEnd).getName())
											);
						newState.setInsideUnion(true);
						mainList.statesList.add(newState);
						
					}
					/****************************************************** PASSO2 - 'aa' LINKA O CHAINEND ******************************************************/
					else {
						/* Uso o Chain End (fim da cadeia) */
						/* Apago o antigo start */
						mainList.statesList.get(positionStart).setChainStart(false);

						/* Faço o chain end linkar no novo estado final */
						mainList.statesList.set(positionEnd, logicState.swapFinalLink(mainList.statesList.get(positionEnd), 
								expression.charAt(i), 
								mainList.statesList.size()));

						/* Crio o novo estado final */
						newState = new State(mainList.statesList.size(),
											"F", 
											new State((-1))
											);
						newState.setChainEnd(true);
						mainList.statesList.add(newState);

						/* Movo o novo end para o último estado e o novo start para o penúltimo */
						mainList.statesList.get(positionEnd).setChainStart(true);
						mainList.statesList.get(positionEnd).setChainEnd(false);
					}
				}
			}
			
			else if(expression.charAt(i) == '(') {
				/****************************************************** PASSO5 - '(' RECURSAO CARAIO ******************************************************/
				ArrayList<State> auxStateList = new ArrayList<State>();
				ConversionER auxiliar = new ConversionER();
				
				/* Utilizo uma lista auxiliar para receber o resultado da recursão */
				auxStateList = mainList.logicFunction(auxiliar, expression.substring(i+1, expression.length())).statesList;
				
				/* Funde as duas listas */
				mainList.statesList.addAll(auxStateList);
				
				/* Seta i com a posição da palavra em que foi terminada a lógica de recursão */
				for (int k = i+1; k < expression.length(); k++) {
					if (expression.charAt(k) == ')') {
						i = k;
						k = expression.length();
					}
				}
			}
			
			else if(expression.charAt(i) == 'U') {
				/****************************************************** PASSO3 - 'aU' ADD FIM INICIO ******************************************************/
				mainList = mainList.foundUnion(mainList, i, expression);
			}
			
			else if(expression.charAt(i) == '*') {
				/****************************************************** PASSO4 - 'a*' ADD FIM INICIO ******************************************************/
				mainList = mainList.foundStarKey(mainList, i, expression);
			}
			
			else if(expression.charAt(i) == ')') {
				/****************************************************** PASSO6 - ')' RETORNA RECURSAO CARAIO ******************************************************/
				return mainList;
			}
			
			else {
				System.out.println("Erro na criação do estado");
			}
		}

		return mainList;

	}
	
	public ConversionER foundUnion(ConversionER conversion, int i, String expression) {
		
		State logicState = new State();
		State newState = new State();
		
		/* Pego a posição do chainstart e verifico se ele é também union end */
		if (conversion.statesList.get(logicState.findChainStart(conversion.statesList)).isUnionEnd()) {
			return conversion.specialUnion(conversion);
		}
		
		else {
			/**************************** PASSO 1 da União - Ultimo estado ****************************/
			/**************************** Adiciono último estado e linko o antigo para ele ****************************/
			int positionEnd = logicState.findChainEnd(conversion.statesList);
			
			/* Faço o chain end linkar no novo estado final e tiro o chainend */
			conversion.statesList.set(positionEnd, logicState.swapFinalLink(conversion.statesList.get(positionEnd), 
																			'L', 
																			conversion.statesList.size()));
			conversion.statesList.get(positionEnd).setChainEnd(false);

			/* Crio o novo estado final */
			newState = new State(conversion.statesList.size(),
					"F", 
					new State((-1))
					);
			newState.setChainEnd(true);
			newState.setUnionEnd(true);
			conversion.statesList.add(newState);

			/**************************** PASSO 2 da União - Primeiro estado ****************************/
			/**************************** Criar o primeiro elemento da união  ****************************/
			int positionStart = logicState.findChainStart(conversion.statesList);

			/* Crio vetor auxiliar */
			ArrayList<State> auxStateList = new ArrayList<State>();
			newState = new State(conversion.statesList.get(positionStart).getName(),
					"L", 
					new State(conversion.statesList.get(positionStart+1).getName())
					);
			newState.setChainStart(true);
			newState.setUnionStart(true);
			auxStateList.add(newState);

			/**************************** PASSO 3 da União - Recortar ****************************/
			/**************************** Recorta do vetor principal para o auxiliar  ****************************/
			boolean control = true;
			while(control) {

				if(positionStart == conversion.statesList.size()) {
					control = false;
				}else {
					auxStateList.add(conversion.statesList.get(positionStart));
					conversion.statesList.remove(positionStart);
				}
			}
			/* Troco o chain start pro primeiro da união */
			auxStateList.get(1).setChainStart(false);


			/**************************** PASSO 4 da União - Renomear ****************************/
			/**************************** Recorta do vetor principal para o auxiliar  ****************************/
			for (int j = 1; j < auxStateList.size(); j++) {
				auxStateList.get(j).setName(auxStateList.get(j).getName()+1);
			}

			for (int j = 1; j < auxStateList.size(); j++) {
				for (int j2 = 0; j2 < auxStateList.get(j).getNextState().size(); j2++) {
					if (auxStateList.get(j).getNextState().get(j2).getName() >= 0 && !auxStateList.get(j).getNextState().get(j2).isUnionEnd() ) {
						auxStateList.get(j).setNextStateName(j2, (auxStateList.get(j).getNextStateName(j2))+1);
					}
				}
			}

			/**************************** PASSO 5 da União - Fuuuusão HA ****************************/
			/**************************** Funde o auxiliar com o principal  ****************************/
			conversion.statesList.addAll(auxStateList);
			/* Verifico e marco o menor UnionStart e o maior UnionEnd  */
			//conversion.statesList = conversion.fixUnionStartAndEnd(conversion.statesList);
			return conversion;
			
		}
		
		
	}

	public ConversionER specialUnion(ConversionER conversion) {
		State newState = new State();
		State logicState = new State();
		
		/**************************** PASSO 1 da União Especial - Primeiro estado ****************************/
		/**************************** Criar o vetor auxiliar  ****************************/
		/* Procuro o chain end para começar o vetor auxiliar a partir do nome dele */
		//int positionStartAux = logicState.findChainEnd(conversion.statesList);
		
		/* Procuro o chain start para copiar e alterar a transição*/
		int positionStart = logicState.findChainStart(conversion.statesList);
		
		/* Procuro a referencia do insideUnion antes do ChainEnd */
		int insideUnionPosition = -1;
		/* inside union -1 porque depois de adicionar um specialKleene or union - voce colocar o UE na frente dos anteriores*/
		for (int i = positionStart; i < conversion.statesList.size(); i++) {
			if (conversion.statesList.get(i).isInsideUnion()) {
				insideUnionPosition = i;
				i = conversion.statesList.size();
			}
		}
		
		/* Valor da transição do unionEnd para o novo chainEnd*/
		//String transitionValue = logicState.findSpecialTransition(conversion.statesList.get(positionStart), conversion.statesList.get(positionStartAux).getName());
		
		/* Crio vetor auxiliar */
		ArrayList<State> auxStateList = new ArrayList<State>();
		
		
		
		if (insideUnionPosition > -1) {
			
			newState = new State(conversion.statesList.get(positionStart).getName(),
							"L", 
							new State(conversion.statesList.get(insideUnionPosition).getName()+1)
							);
			newState.setChainStart(conversion.statesList.get(positionStart).isChainStart());
			newState.setUnionStart(conversion.statesList.get(positionStart).isUnionStart());
			newState.setUnionEnd(conversion.statesList.get(positionStart).isUnionEnd());
			auxStateList.add(newState); 
			
			/**************************** PASSO 3 da União - Recortar ****************************/
			/**************************** Recorta do vetor principal para o auxiliar  ****************************/
			boolean control = true;
			boolean firstCut = false;
			while(control) {
				if(!firstCut) {
					auxStateList.add(conversion.statesList.get(positionStart));
					conversion.statesList.remove(positionStart);
					firstCut = true;
				}
				if(insideUnionPosition == conversion.statesList.size()) {
					control = false;
				}else {
					auxStateList.add(conversion.statesList.get(insideUnionPosition));
					conversion.statesList.remove(insideUnionPosition);
				}
			}
		} 
		
		else {
			newState = new State(conversion.statesList.get(positionStart).getName(),
							"L", 
							new State(conversion.statesList.get(positionStart+1).getName())
							);
			newState.setChainStart(conversion.statesList.get(positionStart).isChainStart());
			newState.setUnionStart(conversion.statesList.get(positionStart).isUnionStart());
			newState.setUnionEnd(conversion.statesList.get(positionStart).isUnionEnd());
			auxStateList.add(newState); 
			
			boolean control = true;
			while(control) {
				if(positionStart == conversion.statesList.size()) {
					control = false;
				}else {
					auxStateList.add(conversion.statesList.get(positionStart));
					conversion.statesList.remove(positionStart);
				}
			}
		}
		
		
		/* Troco o chain start pro primeiro da união */
		
		
		/**************************** PASSO 4 da União - Redistribuir as ligações menores pro inicial ****************************/
		/**************************** Passa as ligações adiante  ****************************/
		for (int i = 0; i < auxStateList.get(0).getNextState().size(); i++) {
			for (int j = 0; j < auxStateList.get(1).getNextState().size(); j++) {
				if (auxStateList.get(0).getNextState().get(i).getName() != auxStateList.get(1).getNextState().get(j).getName() 
						&& (auxStateList.get(0).getNextState().get(i).getName() < auxStateList.get(2).getName())) {
					
					auxStateList.get(0).getNextState().add(new State(auxStateList.get(1).getNextState().get(j).getName()));
					auxStateList.get(0).getItemToChange().add(auxStateList.get(1).getItemToChange().get(j));
					
					auxStateList.get(1).getNextState().remove(j);
					auxStateList.get(1).getItemToChange().remove(j);
					
				}
			}
		}
		
		/* Adciona estado final e linka o antigo final para ele */
		auxStateList.set(auxStateList.size()-1, logicState.swapFinalLink((auxStateList.get(auxStateList.size()-1)), 
																			'L', 
																			auxStateList.get(auxStateList.size()-1).getName()+1)
																			);
		/* Adiciona o estado final */
		newState = new State((auxStateList.get(auxStateList.size()-1).getName()+1),
							"F", 
							new State(-1)
							);
		auxStateList.add(newState);
		
		/**************************** PASSO 5 a União - Renomear ****************************/
		/**************************** Renomeia a pora toda do vetor principal para o auxiliar  ****************************/
		/* Troco os nomes */
		auxStateList.get(1).setName(auxStateList.get(2).getName());
		for (int j = 2; j < auxStateList.size(); j++) {
			auxStateList.get(j).setName(auxStateList.get(j).getName()+1);
		}

		for (int j = 1; j < auxStateList.size(); j++) {
			for (int j2 = 0; j2 < auxStateList.get(j).getNextState().size(); j2++) {
				if (auxStateList.get(j).getNextState().get(j2).getName() >= 0 && !auxStateList.get(j).getNextState().get(j2).isUnionEnd() ) {
					auxStateList.get(j).setNextStateName(j2, (auxStateList.get(j).getNextStateName(j2))+1);
				}
			}
		}
		
		/* Pegar o nextState do chainStart (que aponta para o menor da frente) */
		/* Ir recortando e colando no auxiliar até o chainEnd (ou fim do vetor) */
		/* Depois de recortar tratar a adição no final */
		/* Talvez somar +1 em todos após o primeiro do vetor aux */
		/* Retornar para o segunda perna da união */
		for (int i = 0; i < auxStateList.size(); i++) {
			if (i==0) {
				auxStateList.get(i).setChainStart(true);
				auxStateList.get(i).setUnionStart(true);
			}
			
			else if(i==auxStateList.size()-1) {
				auxStateList.get(i).setChainStart(false);
				auxStateList.get(i).setUnionStart(false);
				auxStateList.get(i).setChainEnd(true);
				auxStateList.get(i).setUnionEnd(true);
			}
			else {
				auxStateList.get(i).setChainStart(false);
				auxStateList.get(i).setUnionStart(false);
				auxStateList.get(i).setChainEnd(false);
				auxStateList.get(i).setUnionEnd(false);
			}
		}
		
		/**************************** PASSO 5 da União - Fuuuusão HA ****************************/
		/**************************** Funde o auxiliar com o principal  ****************************/
		conversion.statesList.addAll(auxStateList);
		/* Verifico e marco o menor UnionStart e o maior UnionEnd  */
		//conversion.statesList = conversion.fixUnionStartAndEnd(conversion.statesList);
		
		return conversion;
	}
	
	public ConversionER foundStarKey(ConversionER conversion, int i, String expression){
		State logicState = new State();
		State newState = new State();
		//Read reader = new Read();
		
		if (conversion.statesList.get(logicState.findChainStart(conversion.statesList)).isUnionEnd()) {
			return conversion.specialStarKey(conversion);
		}
		
		else {
			/**************************** PASSO 6.1 do Kleene - Alterar letra interna a união ****************************/
			/**************************** Ve se o último elemento pertence a uma união e procura o chainEnd  ****************************/
			if (conversion.statesList.get(conversion.getLastState()).isInsideUnion()){ 
				/* Acho o chain end para percorrer e recortar até o unionInside */
				int positionEndUnionEnd = logicState.findChainEnd(conversion.statesList); /* Salvo o antigo positionEnd com UnionEnd*/
				
				/**************************** PASSO 1.1 do Kleene - Removo o link do CE do último estado ****************************/
				/**************************** E linko ele para o novo último ****************************/
				/* Retiro os links do InsideUnion para o elemento CE e UE */
				conversion.statesList.get(conversion.getLastState()).setInsideUnion(false);
				for (int j2 = 0; j2 < conversion.statesList.get(conversion.getLastState()).getNextState().size(); j2++) {
					if (conversion.statesList.get(conversion.getLastState()).getNextState().get(j2).getName() == conversion.statesList.get(positionEndUnionEnd).getName()) {
						conversion.statesList.get(conversion.getLastState()).getNextState().remove(j2);
						conversion.statesList.get(conversion.getLastState()).getItemToChange().remove(j2);
						conversion.statesList.get(conversion.getLastState()).getNextState().add(new State(conversion.statesList.size()));
						conversion.statesList.get(conversion.getLastState()).getItemToChange().add("L");
					}
				}
				
				/**************************** PASSO 2.1 do Kleene - Ultimo estado ****************************/
				/**************************** Adiciono último estado e linko o antigo para ele ****************************/
				/* Crio o novo estado final */
				newState = new State(conversion.statesList.size(),
										"F", 
										new State(-1)
										);
				newState.setInsideUnion(true);
				/* Linko o novo final no 'atual inicial' de ação com Kleene */
				newState.addItemtoChange("L");
				newState.addNextState(new State(conversion.statesList.get(positionEndUnionEnd+1).getName()));
				/* Adiciono na lista */
				conversion.statesList.add(newState);
				
				
				/**************************** PASSO 3.1 do Kleene - Recorto pro AUX****************************/
				/**************************** Começou o ctrl+x ****************************/
				ArrayList<State> auxStateList = new ArrayList<State>();
				
				/* Adiciono o novo inicial no auxiliar */
				//newState = new State();
				newState = new State(conversion.statesList.get(positionEndUnionEnd+1).getName(),
										"L", 
										new State(conversion.statesList.get(positionEndUnionEnd+2).getName())
										);
				
				/**************************** PASSO 3.2 do Kleene - Transfere os nextStates****************************/
				/**************************** Os nexts do antigo inicial pro novo inicial ****************************/
				/* Adiciona o nextState diferente do novo inicial e remove os do antigo inicial */
				for (int j2 = 0; j2 < conversion.statesList.get(positionEndUnionEnd+1).getNextState().size(); j2++) {
					if (newState.getNextState().get(0).getName() != conversion.statesList.get(positionEndUnionEnd+1).getNextState().get(j2).getName()) {
						newState.getNextState().add(conversion.statesList.get(positionEndUnionEnd+1).getNextState().get(j2));
						newState.getItemToChange().add(conversion.statesList.get(positionEndUnionEnd+1).getItemToChange().get(j2));
						conversion.statesList.get(positionEndUnionEnd+1).getNextState().remove(j2);
						conversion.statesList.get(positionEndUnionEnd+1).getItemToChange().remove(j2);
					}
				}
				
				/* Adiciono novo state com estados adicionados */
				auxStateList.add(newState);
				
				/* Recorto do original pro auxiliar */
				boolean control = true;
				while(control) {
					if(positionEndUnionEnd+1 == conversion.statesList.size()) {
						control = false;
					}else {
						auxStateList.add(conversion.statesList.get(positionEndUnionEnd+1));
						conversion.statesList.remove(positionEndUnionEnd+1);
					}
				}
				
				/**************************** PASSO 4 do Kleene - Renomear ****************************/
				/**************************** Renomeia a pora toda do vetor principal para o auxiliar  ****************************/
				/* Troco os nomes */
				for (int j = 1; j < auxStateList.size(); j++) {
					auxStateList.get(j).setName(auxStateList.get(j).getName()+1);
				}

				for (int j = 1; j < auxStateList.size(); j++) {
					for (int j2 = 0; j2 < auxStateList.get(j).getNextState().size(); j2++) {
						if (auxStateList.get(j).getNextState().get(j2).getName() >= 0 && !auxStateList.get(j).getNextState().get(j2).isUnionEnd() ) {
							auxStateList.get(j).setNextStateName(j2, (auxStateList.get(j).getNextStateName(j2))+1);
						}
					}
				}
				
				/**************************** PASSO 5 do Kleene - Linkar o CS com o CE ****************************/
				/**************************** Linka lá no fim saporra  ****************************/
				/* Adição da nova transição do novo incial pro novo final do vetor aux */
				/* Verifico se o primeiro elemento linka o último do vetor auxiliar por padrão */
				/* Caso houver o link - vou setar a variável de hasLink como true e impedir que essa ligação seja duplicada */
				boolean hasLink = false;
				for (int k = 0; k < auxStateList.get(0).getNextState().size(); k++) {
					if (auxStateList.get(0).getNextState().get(k).getName() == auxStateList.get(auxStateList.size()-1).getName()) {
						hasLink = true;
					}
				}
				
				/**************************** PASSO 6 do Kleene - Linkar o último elemento com o UnionEnd ****************************/
				/**************************** Linka lá no fim da união de riba!  ****************************/
				/* Adição da nova transição do novo incial pro novo final do vetor aux */
				/* Caso não houver o link - adiciono normalmente */
				if (!hasLink) {
					auxStateList.get(0).addNextState(new State(auxStateList.get(auxStateList.size()-1).getName()));
					auxStateList.get(0).addItemtoChange("L");
				}
				
				auxStateList.set(auxStateList.size()-1, logicState.swapFinalLink((auxStateList.get(auxStateList.size()-1)), 
																				'L', 
																				positionEndUnionEnd)
								);
				
				/**************************** PASSO 7 do Kleene - Fuuuusão HA ****************************/
				/**************************** Funde o auxiliar com o principal  ****************************/
				/* Adição do vetor auxiliar no original */
				conversion.statesList.addAll(auxStateList);
				return conversion;
			}
			
			else {
				/**************************** PASSO 1 do Kleene - Ultimo estado ****************************/
				/**************************** Adiciono último estado e linko o antigo para ele ****************************/
				int positionEnd = logicState.findChainEnd(conversion.statesList);
				
				/* Faço o chain end linkar no novo estado final e tiro o chainend */
				conversion.statesList.set(positionEnd, logicState.swapFinalLink(conversion.statesList.get(positionEnd), 
																				'L', 
																				conversion.statesList.size()));
				conversion.statesList.get(positionEnd).setChainEnd(false);
				
				/* Crio o novo estado final */
				newState = new State(conversion.statesList.size(),
										"F", 
										new State((-1))
										);
				newState.setChainEnd(true);
				conversion.statesList.add(newState);
				
				/**************************** PASSO 2 do Kleene - Linko o novo chainEnd no antigo(atual) chainStart ****************************/
				/**************************** Linko o novo chainEnd no antigo(atual) chainStart ****************************/
				int positionStart = logicState.findChainStart(conversion.statesList);
				positionEnd = logicState.findChainEnd(conversion.statesList);
						
				/* adiciono o qce -L-> qcs */
				conversion.statesList.get(positionEnd).addItemtoChange("L");
				conversion.statesList.get(positionEnd).addNextState(new State(conversion.statesList.get(positionStart).getName()));
				
				
				/**************************** PASSO 3 do Kleene - Recorto pro AUX****************************/
				/**************************** Começou o ctrl+x ****************************/
				ArrayList<State> auxStateList = new ArrayList<State>();
				
				/* Adiciono o novo inicial no auxiliar */
				//newState = new State();
				newState = new State(conversion.statesList.get(positionStart).getName(),
								"L", 
								new State(conversion.statesList.get(positionStart+1).getName())
								);
				
				/**************************** PASSO 3.1 do Kleene - Transfere os nextStates****************************/
				/**************************** Os nexts do antigo inicial pro novo inicial ****************************/
				/* Adiciona o nextState diferente do novo inicial e remove os do antigo inicial */
				for (int j2 = 0; j2 < conversion.statesList.get(positionStart).getNextState().size(); j2++) {
					if (newState.getNextState().get(0).getName() != conversion.statesList.get(positionStart).getNextState().get(j2).getName()) {
						newState.getNextState().add(conversion.statesList.get(positionStart).getNextState().get(j2));
						newState.getItemToChange().add(conversion.statesList.get(positionStart).getItemToChange().get(j2));
						conversion.statesList.get(positionStart).getNextState().remove(j2);
						conversion.statesList.get(positionStart).getItemToChange().remove(j2);
					}
				}
				
				newState.setChainStart(true);
				auxStateList.add(newState);
				
				/* Recorto do original pro auxiliar */
				boolean control = true;
				while(control) {
					if(positionStart == conversion.statesList.size()) {
						control = false;
					}else {
						auxStateList.add(conversion.statesList.get(positionStart));
						conversion.statesList.remove(positionStart);
					}
				}
				auxStateList.get(1).setChainStart(false);
				
				/**************************** PASSO 4 do Kleene - Renomear ****************************/
				/**************************** Renomeia a pora toda do vetor principal para o auxiliar  ****************************/
				/* Troco os nomes */
				for (int j = 1; j < auxStateList.size(); j++) {
					auxStateList.get(j).setName(auxStateList.get(j).getName()+1);
				}

				for (int j = 1; j < auxStateList.size(); j++) {
					for (int j2 = 0; j2 < auxStateList.get(j).getNextState().size(); j2++) {
						if (auxStateList.get(j).getNextState().get(j2).getName() >= 0 && !auxStateList.get(j).getNextState().get(j2).isUnionEnd() ) {
							auxStateList.get(j).setNextStateName(j2, (auxStateList.get(j).getNextStateName(j2))+1);
						}
					}
				}
				
				/**************************** PASSO 5 do Kleene - Linkar o CS com o CE ****************************/
				/**************************** Linka lá no fim saporra  ****************************/
				/* Adição da nova transição do novo incial pro novo final do vetor aux */
				auxStateList.get(0).addNextState(new State(auxStateList.get(auxStateList.size()-1).getName()));
				auxStateList.get(0).addItemtoChange("L");
				
				/**************************** PASSO 6 do Kleene - Fuuuusão HA ****************************/
				/**************************** Funde o auxiliar com o principal  ****************************/
				/* Adição do vetor auxiliar no original */
				conversion.statesList.addAll(auxStateList);
				return conversion;
			}
		}
	}
	
	public ConversionER specialStarKey(ConversionER conversion) {
		State logicState = new State();
		State newState = new State();
		
		/* Procuro o chain start para copiar e alterar a transição*/
		int positionStart = logicState.findChainStart(conversion.statesList);

		/* Procuro a referencia do insideUnion antes do ChainEnd */
		int insideUnionPosition = -1;
		for (int i = positionStart; i < conversion.statesList.size(); i++) {
			if (conversion.statesList.get(i).isInsideUnion()) {
				insideUnionPosition = i;
				i = conversion.statesList.size();
			}
		}
		
		/* Procuro o chain start para copiar e alterar a transição*/
		int positionEnd = logicState.findChainEnd(conversion.statesList);
		int unionEndName = conversion.statesList.get(positionEnd).getName();

		/* Crio vetor auxiliar */
		ArrayList<State> auxStateList = new ArrayList<State>();
		
		boolean control = true;
		boolean firstCut = false;
		
		if (conversion.statesList.get(positionStart).getName() < conversion.statesList.get(positionStart-1).getName() ) {
			
			newState = new State(conversion.statesList.get(positionStart).getName(),
					"L", 
					new State(conversion.statesList.get(positionEnd).getName()+1)
					);
			newState.setChainStart(conversion.statesList.get(positionStart).isChainStart());
			newState.setUnionStart(conversion.statesList.get(positionStart).isUnionStart());
			newState.setUnionEnd(conversion.statesList.get(positionStart).isUnionEnd());
			auxStateList.add(newState);
			
			/**************************** PASSO 3 da União - Recortar ****************************/
			/**************************** Recorta do vetor principal para o auxiliar  ****************************/
			while(control) {
				if(!firstCut) {
					auxStateList.add(conversion.statesList.get(positionStart));
					conversion.statesList.remove(positionStart);
					firstCut = true;
				}
				if(positionEnd == conversion.statesList.size()) {
					control = false;
				}else {
					auxStateList.add(conversion.statesList.get(positionEnd));
					conversion.statesList.remove(positionEnd);
				}
			}
			
			/**************************** PASSO 4 da União - Redistribuir as ligações menores pro inicial ****************************/
			/**************************** Passa as ligações adiante  ****************************/
			for (int i = 0; i < auxStateList.get(0).getNextState().size(); i++) {
				for (int j = 0; j < auxStateList.get(1).getNextState().size(); j++) {
					/* a logica de aUbcUd* - ta bugando! o 9 != 6 e 9 menor que 9 ta comendo! */
					if (auxStateList.get(0).getNextState().get(i).getName() != auxStateList.get(1).getNextState().get(j).getName() 
							&& (auxStateList.get(1).getNextState().get(j).getName() < auxStateList.get(2).getName())) {
						
						auxStateList.get(0).getNextState().add(new State(auxStateList.get(1).getNextState().get(j).getName()));
						auxStateList.get(0).getItemToChange().add(auxStateList.get(1).getItemToChange().get(j));
						
						auxStateList.get(1).getNextState().remove(j);
						auxStateList.get(1).getItemToChange().remove(j);
						
					}
				}
			}
			
			/* Adciona estado final e linka o antigo final para ele */
			auxStateList.set(auxStateList.size()-1, logicState.swapFinalLink((auxStateList.get(auxStateList.size()-1)), 
																				'L', 
																				auxStateList.get(auxStateList.size()-1).getName()+1)
																				);
			
			/* Tira o link para antigo union end - e redireciona para o novo estado final do kleene */
			for (int i = 0; i < auxStateList.get(auxStateList.size()-1).getNextState().size(); i++) {
				if (auxStateList.get(auxStateList.size()-1).getNextState().get(i).getName() == unionEndName) {
					auxStateList.get(auxStateList.size()-1).setNextStateName(i, auxStateList.get(auxStateList.size()-1).getName()+1);
				}
			}
			
			/* Adiciona o estado final e linko no fim da união */
			newState = new State((auxStateList.get(auxStateList.size()-1).getName()+1),
								"L", 
								new State(unionEndName-1)
								);
			auxStateList.add(newState);
			
			/**************************** PASSO 5 a União - Renomear ****************************/
			/**************************** Renomeia a pora toda do vetor principal para o auxiliar  ****************************/
			/* Troco os nomes */
			auxStateList.get(1).setName(auxStateList.get(2).getName());
			for (int j = 2; j < auxStateList.size(); j++) {
				auxStateList.get(j).setName(auxStateList.get(j).getName()+1);
			}

			for (int j = 1; j < auxStateList.size(); j++) {
				for (int j2 = 0; j2 < auxStateList.get(j).getNextState().size(); j2++) {
					if (auxStateList.get(j).getNextState().get(j2).getName() >= 0 && !auxStateList.get(j).getNextState().get(j2).isUnionEnd() ) {
						auxStateList.get(j).setNextStateName(j2, (auxStateList.get(j).getNextStateName(j2))+1);
					}
				}
			}
			
			/* Limpeza */
			for (int i = 0; i < auxStateList.size(); i++) {
				if (i==0) {
					auxStateList.get(i).setChainStart(true);
				}
				
				else if(i==auxStateList.size()-1) {
					auxStateList.get(i).setChainStart(false);
					auxStateList.get(i).setUnionStart(false);
					auxStateList.get(i).setChainEnd(false);
					auxStateList.get(i).setUnionEnd(false);
					auxStateList.get(i).setInsideUnion(true);
				}
				else {
					auxStateList.get(i).setChainStart(false);
					auxStateList.get(i).setUnionStart(false);
					auxStateList.get(i).setChainEnd(false);
					auxStateList.get(i).setUnionEnd(false);
					auxStateList.get(i).setInsideUnion(false);
				}
			}
			
			/* Linka o estado final para o antigo inicial */
			auxStateList.get(auxStateList.size()-1).getNextState().add(new State(auxStateList.get(2).getName()));
			auxStateList.get(auxStateList.size()-1).getItemToChange().add("L");
			
			/**************************** PASSO 5 do Kleene - Linkar o CS com o CE ****************************/
			/**************************** Linka lá no fim saporra  ****************************/
			/* Adição da nova transição do novo incial pro novo final do vetor aux */
			/* Verifico se o primeiro elemento linka o último do vetor auxiliar por padrão */
			/* Caso houver o link - vou setar a variável de hasLink como true e impedir que essa ligação seja duplicada */
			boolean hasLink = false;
			for (int k = 0; k < auxStateList.get(1).getNextState().size(); k++) {
				if (auxStateList.get(1).getNextState().get(k).getName() == auxStateList.get(auxStateList.size()-1).getName()) {
					hasLink = true;
				}
			}
			
			/**************************** PASSO 6 do Kleene - Linkar o último elemento com o UnionEnd ****************************/
			/**************************** Linka lá no fim da união de riba!  ****************************/
			/* Adição da nova transição do novo incial pro novo final do vetor aux */
			/* Caso não houver o link - adiciono normalmente */
			if (!hasLink) {
				auxStateList.get(1).addNextState(new State(auxStateList.get(auxStateList.size()-1).getName()));
				auxStateList.get(1).addItemtoChange("L");
			}
		} 
		else {
			newState = new State(conversion.statesList.get(positionStart).getName(),
					"L", 
					new State(conversion.statesList.get(insideUnionPosition).getName()+1)
					);
			newState.setChainStart(conversion.statesList.get(positionStart).isChainStart());
			newState.setUnionStart(conversion.statesList.get(positionStart).isUnionStart());
			newState.setUnionEnd(conversion.statesList.get(positionStart).isUnionEnd());
			auxStateList.add(newState);
			
			/**************************** PASSO 3 da União - Recortar ****************************/
			/**************************** Recorta do vetor principal para o auxiliar  ****************************/
			while(control) {
				if(!firstCut) {
					auxStateList.add(conversion.statesList.get(positionStart));
					conversion.statesList.remove(positionStart);
					firstCut = true;
				}
				if(insideUnionPosition == conversion.statesList.size()) {
					control = false;
				}else {
					auxStateList.add(conversion.statesList.get(insideUnionPosition));
					conversion.statesList.remove(insideUnionPosition);
				}
			}
			
			/**************************** PASSO 4 da União - Redistribuir as ligações menores pro inicial ****************************/
			/**************************** Passa as ligações adiante  ****************************/
			for (int i = 0; i < auxStateList.get(0).getNextState().size(); i++) {
				for (int j = 0; j < auxStateList.get(1).getNextState().size(); j++) {
					if (auxStateList.get(0).getNextState().get(i).getName() != auxStateList.get(1).getNextState().get(j).getName() 
							&& (auxStateList.get(0).getNextState().get(i).getName() < auxStateList.get(2).getName())) {
						
						auxStateList.get(0).getNextState().add(new State(auxStateList.get(1).getNextState().get(j).getName()));
						auxStateList.get(0).getItemToChange().add(auxStateList.get(1).getItemToChange().get(j));
						
						auxStateList.get(1).getNextState().remove(j);
						auxStateList.get(1).getItemToChange().remove(j);
						
					}
				}
			}
			
			/* Adciona estado final e linka o antigo final para ele */
			auxStateList.set(auxStateList.size()-1, logicState.swapFinalLink((auxStateList.get(auxStateList.size()-1)), 
																				'L', 
																				auxStateList.get(auxStateList.size()-1).getName()+1)
																				);
			/* Adiciona o estado final */
			newState = new State((auxStateList.get(auxStateList.size()-1).getName()+1),
								"F", 
								new State(-1)
								);
			auxStateList.add(newState);
			
			/**************************** PASSO 5 a União - Renomear ****************************/
			/**************************** Renomeia a pora toda do vetor principal para o auxiliar  ****************************/
			/* Troco os nomes */
			auxStateList.get(1).setName(auxStateList.get(2).getName());
			for (int j = 2; j < auxStateList.size(); j++) {
				auxStateList.get(j).setName(auxStateList.get(j).getName()+1);
			}

			for (int j = 1; j < auxStateList.size(); j++) {
				for (int j2 = 0; j2 < auxStateList.get(j).getNextState().size(); j2++) {
					if (auxStateList.get(j).getNextState().get(j2).getName() >= 0 && !auxStateList.get(j).getNextState().get(j2).isUnionEnd() ) {
						auxStateList.get(j).setNextStateName(j2, (auxStateList.get(j).getNextStateName(j2))+1);
					}
				}
			}
			
			/* Limpeza */
			for (int i = 0; i < auxStateList.size(); i++) {
				if (i==0) {
					auxStateList.get(i).setChainStart(true);
				}
				
				else if(i==auxStateList.size()-1) {
					auxStateList.get(i).setChainStart(false);
					auxStateList.get(i).setUnionStart(false);
					auxStateList.get(i).setChainEnd(true);
					auxStateList.get(i).setUnionEnd(false);
				}
				else {
					auxStateList.get(i).setChainStart(false);
					auxStateList.get(i).setUnionStart(false);
					auxStateList.get(i).setChainEnd(false);
					auxStateList.get(i).setUnionEnd(false);
				}
			}
			
			/* Linka o estado final para o antigo inicial */
			auxStateList.get(auxStateList.size()-1).getNextState().add(new State(auxStateList.get(1).getName()));
			auxStateList.get(auxStateList.size()-1).getItemToChange().add("L");
			
			/**************************** PASSO 5 do Kleene - Linkar o CS com o CE ****************************/
			/**************************** Linka lá no fim saporra  ****************************/
			/* Adição da nova transição do novo incial pro novo final do vetor aux */
			/* Verifico se o primeiro elemento linka o último do vetor auxiliar por padrão */
			/* Caso houver o link - vou setar a variável de hasLink como true e impedir que essa ligação seja duplicada */
			boolean hasLink = false;
			for (int k = 0; k < auxStateList.get(0).getNextState().size(); k++) {
				if (auxStateList.get(0).getNextState().get(k).getName() == auxStateList.get(auxStateList.size()-1).getName()) {
					hasLink = true;
				}
			}
			
			/**************************** PASSO 6 do Kleene - Linkar o último elemento com o UnionEnd ****************************/
			/**************************** Linka lá no fim da união de riba!  ****************************/
			/* Adição da nova transição do novo incial pro novo final do vetor aux */
			/* Caso não houver o link - adiciono normalmente */
			if (!hasLink) {
				auxStateList.get(0).addNextState(new State(auxStateList.get(auxStateList.size()-1).getName()));
				auxStateList.get(0).addItemtoChange("L");
			}
		}
		
		/**************************** PASSO 6 do Kleene - Fuuuusão HA ****************************/
		/**************************** Funde o auxiliar com o principal  ****************************/
		/* Adição do vetor auxiliar no original */
		conversion.statesList.addAll(auxStateList);
		
		return conversion;
	}
	
	public ConversionER foundRightParenthesis(ConversionER conversion) {
		System.out.println("ENTREI NO IF COM a lista de tamanho "+conversion.statesList.size());
		System.out.println("Entrei com o custo do primeiro elemento sendo " + conversion.statesList.get(0).getItemToChange().get(0));
		
		if (conversion.statesList.size() > 0 && conversion.statesList.get(0).isUnionStart()) {
			
			int startPosition = conversion.statesList.size()-1;
			//int endPosition = 0;
			
			for (int j = 0; j < conversion.statesList.size(); j++) {
				System.out.println("ESTOU RODANDO O ELEMENTO Q"+conversion.statesList.get(j).getName());
				if (conversion.statesList.get(j).isChainStart() && (conversion.statesList.get(j).getName() < conversion.statesList.get(startPosition).getName())) {
					startPosition = j;
				}
				
				else if(conversion.statesList.get(j).isChainStart()){
					conversion.statesList.get(j).setChainStart(false);
				}
				
				else if(conversion.statesList.get(j).isUnionEnd() && (j == conversion.statesList.size()-1)) {
					//endPosition = j;
					conversion.statesList.get(j).setChainEnd(true);
				}
				
				else if(conversion.statesList.get(j).isUnionEnd()){
					conversion.statesList.get(j).setChainEnd(false);
				}
				
				else if(conversion.statesList.get(j).isChainEnd()) {
					conversion.statesList.get(j).setChainEnd(false);
				}
			}
			
			if (startPosition > 0 && startPosition != conversion.statesList.size()) {
				conversion.statesList.get(startPosition-1).setChainStart(true);
				conversion.statesList.get(startPosition).setChainStart(false);
			}
			
			/*if (endPosition > 0) {
				mainList.statesList.get(endPosition).setChainEnd(true);
			}*/
			
			conversion.statesList.get(conversion.statesList.size()-1).setInsideUnion(true);
		}
		
		return conversion;
	}
	
	/* mudar para q0 */
	public ArrayList<State> setInitialOrEnd(ArrayList<State> states) {
		states.get(0).setInicial(true);
		
		for (int i = 0; i < states.size(); i++) {
			for (int j = 0; j < states.get(i).getNextState().size(); j++) {
				if (states.get(i).getNextStateName(j) == -1) {
					states.get(i).setFinal(true);
					return states;
				}
			}
		}
		
		return states;
	}
	
	public void printTudo(ArrayList<State> auxStateList) {
		System.out.println();
		System.out.println("VOU IMPRIMIR O AUXILIAR PRA SABER DO ROLE!");
		for (int j = 0; j < auxStateList.size(); j++) {
			auxStateList.get(j).printState();
		}
		System.out.println("IMPRIMI O AUXILIAR PRA SABER DO ROLE!");
		System.out.println();
	}
	
	public int getLastState() {
		return this.statesList.size()-1;
	}

	public ArrayList<State> fixUnionStartAndEnd(ArrayList<State> stateList){
		int smallStart = stateList.size()-1;
		int biggestEnd = 0;
		
		for (int i = 0; i < stateList.size(); i++) {
			if (stateList.get(i).isUnionStart() && (stateList.get(i).getName() < stateList.get(smallStart).getName())) {
				smallStart = i;
			} 
			else if(stateList.get(i).isUnionStart()){
				stateList.get(i).setUnionStart(false);
			}
			else if(stateList.get(i).isUnionEnd() && (biggestEnd == 0)) {
				biggestEnd = i;
			}
			else if(stateList.get(i).isUnionEnd() && (stateList.get(i).getName() > stateList.get(biggestEnd).getName())) {
				stateList.get(biggestEnd).setUnionEnd(false);
				biggestEnd = i;
			}
		}
		
		return stateList;
	}

	public void writeGraphviz(ConversionER conversion) {
		FileWriter arquivo;
		
		for (int i = 0; i < conversion.statesList.size(); i++) {
			for (int j = 0; j < conversion.statesList.get(i).getItemToChange().size(); j++) {
				try {
					
					arquivo = new FileWriter(new File("imprimirAbagacaDeCompiladores.dot"),true);
					arquivo.write("q"+conversion.statesList.get(i).getName() + " -" + 
							conversion.statesList.get(i).getItemToChange().get(j) + "> " + 
							"q"+conversion.statesList.get(i).getNextState().get(j).getName() + ""  );
					
					arquivo.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}


			}
		}
	}
}

