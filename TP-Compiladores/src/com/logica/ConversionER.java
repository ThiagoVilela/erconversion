package com.logica;

import java.util.ArrayList;
import java.util.Scanner;

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

		/* Talvez d� pala com mais par�nteses */
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
	}

	public ConversionER logicFunction(ConversionER mainList, String expression) {
		State logicState = new State();
		State newState = new State();
		Read reader = new Read();

		for (int i = 0; i < expression.length(); i++) {
			/****************************************************** REFATORA��O TOTAL ******************************************************/
			
			
			
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

					/* Crio a liga��o e o estado final */
					newState = new State(1,
							"F", 
							new State((-1))
							);
					newState.setChainEnd(true);
					mainList.statesList.add(newState);
				}
				/****************************************************** PASSO2 - 'aa' LINKA O CHAINEND ******************************************************/
				else if(mainList.statesList.size() > 0) {
					/* Acha o Chain End (fim da cadeia) */
					
					int positionStart = logicState.findChainStart(mainList.statesList);
					int positionEnd = logicState.findChainEnd(mainList.statesList);
					
					/* Apago o antigo start */
					mainList.statesList.get(positionStart).setChainStart(false);
					
					/* Fa�o o chain end linkar no novo estado final */
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

					/* Movo o novo end para o �ltimo estado e o novo start para o pen�ltimo */
					mainList.statesList.get(positionEnd).setChainStart(true);
					mainList.statesList.get(positionEnd).setChainEnd(false);
				}
			}
			
			else if(expression.charAt(i) == 'U') {
				/****************************************************** PASSO3 - 'aU' ADD FIM INICIO ******************************************************/
				mainList = mainList.foundUnion(mainList, i, expression);
			}
			
			else {
				System.out.println("Erro na cria��o do estado");
			}
		}

		return mainList;

	}
	
	public ConversionER foundUnion(ConversionER conversion, int i, String expression) {
		
		State logicState = new State();
		State newState = new State();
		
		/**************************** PASSO 1 da Uni�o - Ultimo estado ****************************/
		/**************************** Adiciono �ltimo estado e linko o antigo para ele ****************************/
		int positionEnd = logicState.findChainEnd(conversion.statesList);
		
		/* Fa�o o chain end linkar no novo estado final e tiro o chainend */
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

		/**************************** PASSO 2 da Uni�o - Primeiro estado ****************************/
		/**************************** Criar o primeiro elemento da uni�o  ****************************/
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
		
		/**************************** PASSO 3 da Uni�o - Recortar ****************************/
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
		/* Troco o chain start pro primeiro da uni�o */
		auxStateList.get(1).setChainStart(false);
		
		/**************************** PASSO 4 da Uni�o - Renomear ****************************/
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
		/**************************** PASSO 5 da Uni�o - Fus�o HA ****************************/
		/**************************** Funde o auxiliar com o principal  ****************************/
		conversion.statesList.addAll(auxStateList);
		return conversion;
		
		/*
		if (conversion.statesList.get(conversion.getLastState()).isInsideUnion()) {
			for (int j = conversion.statesList.size()-1; j > 0 ; j--) {
				if (conversion.statesList.get(j).isUnionEnd()) {
					
					
					nextState = new State(-1);
					
					newState = new State(conversion.statesList.size(),
										null, 
										nextState
										);
					newState.setUnionEnd(true);
					conversion.statesList.add(newState);
					
					System.out.println("VOU LINKAR O ESTADO q"+conversion.statesList.get(j).getName()+ " COM q" +newState.getName());
					newState.printState();
					/* REAL *//*
					conversion.statesList.get(j).getItemToChange().add("L");
					conversion.statesList.get(j).getNextState().add(newState);
					
					/* REMOVER O -1 E NULL DA LIGA��O *//*
					for (int j2 = 0; j2 < conversion.statesList.get(j).getNextState().size(); j2++) {
						if (conversion.statesList.get(j).getNextState().get(j2).getName() == -1 && conversion.statesList.get(j).getItemToChange().get(j2) == null) {
							System.out.println("ENTREI NO IF");
							conversion.statesList.get(j).getNextState().remove(j2);
							conversion.statesList.get(j).getItemToChange().remove(j2);
							j2 = conversion.statesList.size();
						}
					}
					/* REMOVER A �LTIMA TRANSI��O Q-1 - VOLTAR AQUI */
					/* REAL *//*
					conversion.statesList.get(j).printState();
					System.out.println("TERMINEI O IF * DENTRO DO U");
					
					j = 0;
				}
			}
		}
		else if (conversion.statesList.get(conversion.getLastState()).isChainEnd()) {
			
			nextState = new State(-1);
			
			newState = new State(conversion.statesList.size(), 
								null, 
								nextState
								);
			newState.setUnionEnd(true);
			conversion.statesList.add(newState);
			
			System.out.println("VOU LINKAR O ESTADO q"+conversion.statesList.get(conversion.statesList.size()-2).getName()+ " COM q"+newState.getName());
			newState.printState();
			/* REAL *//*
			conversion.statesList.get(conversion.statesList.size()-2).getItemToChange().add("L");
			conversion.statesList.get(conversion.statesList.size()-2).getNextState().add(newState);
			/* REAL *//*
			conversion.statesList.get(conversion.statesList.size()-2).printState();
			System.out.println("TERMINEI O IF * DENTRO DO U");
		
		}else {
			nextState = new State(conversion.statesList.size()+1);
			
			newState = new State(conversion.statesList.size(),
								"L", 
								nextState
								);
			newState.setUnionEnd(true);
			conversion.statesList.add(newState);
		}
		*/
	}
	
	public ConversionER foundStarKey(ConversionER conversion, int i, String expression){
		State nextState = new State();
		State newState = new State();
		
		/* Primeira parte do fecho de Kleene */
		nextState = new State(conversion.statesList.size()+1);
		
		newState = new State(conversion.statesList.size(),
							"L", 
							nextState
							);
		
		conversion.statesList.add(newState);
		
		/* Segunda parte do fecho de Kleene */
		/* Acha a posi��o elemento inicial da cadeia que ser� afetada por Kleene */
		int startChainPosition = 0;
		for (int j = conversion.statesList.size()-1; j >= 0 ; j--) {
			if(conversion.statesList.get(j).isChainStart()) {
				startChainPosition = j;
				j = -1;
				/* Achou a posi��o elemento inicial da cadeia que ser� afetada por Kleene */
			}
			
		}
		
		nextState = new State(conversion.statesList.get(startChainPosition).getName());
		
		newState = new State(conversion.statesList.size(),
							"L", 
							nextState
							);
		
		conversion.statesList.add(newState);
		
		/* Terceira parte do fecho de Kleene */
		ArrayList<State> auxStateList = new ArrayList<State>();
		
		nextState = new State(conversion.statesList.get(startChainPosition+1).getName());
		
		newState = new State(startChainPosition,
							"L", 
							nextState
							);
		
		newState.setChainStart(true);
		auxStateList.add(newState);
		
		/* Recorto do original pro auxiliar */
		nextState = new State(conversion.statesList.get(startChainPosition+1).getName());
		
		boolean control = true;
		while(control) {
			auxStateList.add(conversion.statesList.get(startChainPosition));
			conversion.statesList.remove(startChainPosition);
			
			if(startChainPosition+1 == conversion.statesList.size()) {
				conversion.statesList.remove(startChainPosition);
				control = false;
			}
		}
		
		/* Troco os nomes */
		for (int j = 1; j < auxStateList.size(); j++) {
			auxStateList.get(j).setName(auxStateList.get(j).getName()+1);
			for (int j2 = 0; j2 < auxStateList.get(j).getNextState().size(); j2++) {
				if ((auxStateList.get(j).getNextState().get(j2).getName() < auxStateList.get(j).getName())
						&& (auxStateList.get(j).getItemToChange().get(j2) == "L")) {
					
					auxStateList.get(j-1).addNextState(auxStateList.get(j).getNextState().get(j2));
					auxStateList.get(j-1).addItemtoChange(auxStateList.get(j).getItemToChange().get(j2));
					
					auxStateList.get(j).getNextState().remove(j2);
					auxStateList.get(j).getItemToChange().remove(j2);
					
				}
				
				auxStateList.get(j).setNextStateName(j2, (auxStateList.get(j).getNextStateName(j2))+1);
			}
		}
		/* Adi��o o �ltimo estado do vetor aux apontando pro antigo inicial */
		nextState = new State(auxStateList.get(1).getName());
		
		newState = new State(auxStateList.size()+conversion.statesList.size(),
							"L",
							nextState
							);
		auxStateList.add(newState);
		
		/* Adi��o da nova transi��o do novo incial pro novo final do vetor aux */
		
		auxStateList.get(0).addNextState(new State(auxStateList.get(auxStateList.size()-1).getName()));
		auxStateList.get(0).addItemtoChange("L");
		
		/* Realizo as marca��es para controle de l�gica do inicio e fim do Kleene */
		auxStateList.get(0).setChainStart(true);
		auxStateList.get(1).setChainStart(false);
		auxStateList.get(auxStateList.size()-1).setChainEnd(true);
		
		/* Adi��o do vetor auxiliar no original */
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
		states.get(states.size()-1).setFinal(true);
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
}
