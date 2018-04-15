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

		System.out.println();
		for (int i = 0; i < conversion.statesList.size(); i++) {
			if (conversion.statesList.get(i).isChainStart()) {
				System.out.println("ESSE EMBAIXO E START!");
			} else if (conversion.statesList.get(i).isChainEnd()) {
				System.out.println("ESSE EMBAIXO E END!");
			} else if (conversion.statesList.get(i).isUnionStart()) {
				System.out.println("ESSE EMBAIXO E UNION START!");
			} else if (conversion.statesList.get(i).isUnionEnd()) {
				System.out.println("ESSE EMBAIXO E UNION END!");
			}
			conversion.statesList.get(i).printState();
		}
	}
	
	public ConversionER logicFunction(ConversionER mainList, String expression) {
		State nextState = new State();
		State newState = new State();
		Read reader = new Read();
		
		for (int i = 0; i < expression.length(); i++) {
			if (reader.isLetterLower(expression.charAt(i))) {
				if (mainList.statesList.size() == 0) {

					nextState = new State((i+1),
										false,
										mainList.isFinal(i, expression.length())
										);
					
					newState = new State(i,
										mainList.isInitial(mainList.statesList.size()),
										false, 
										Character.toString(expression.charAt(i)), 
										nextState
										);
					
					if(reader.validNext(i, expression.length())) {
						if(expression.charAt(i+1) == '*' || reader.isReservedWord(expression.charAt(i+1))) {
							newState.setChainStart(true);
						}
					}
					
					newState.setChainStart(true);
					mainList.statesList.add(newState);
				}
				
				else if(i!=0 && expression.charAt(i-1) == '*') {
					System.out.println("Nao � o primeiro elemento");
					nextState = new State(mainList.statesList.size(),
											false,
											mainList.isFinal(i, expression.length())
											);
					mainList.statesList.get(mainList.statesList.size()-1).addItemtoChange(Character.toString(expression.charAt(i)));
					mainList.statesList.get(mainList.statesList.size()-1).addNextState(nextState);
					
					if(reader.validNext(i, expression.length())) {
						if(expression.charAt(i+1) == '*' || reader.isReservedWord(expression.charAt(i+1))) {
							mainList.statesList.get(mainList.statesList.size()-1).setChainStart(true);
							//mainList.statesList.get(mainList.statesList.size()-1).setChainEnd(true);
						}
					}
					
				}
				
				else if(i!=0 && expression.charAt(i-1) == 'U') {
					if (reader.validNext(i, expression.length()) && expression.charAt(i+1) == '*') {
						nextState = new State(mainList.statesList.size()+1,
											false,
											mainList.isFinal(i, expression.length())
											);
						
						newState = new State(mainList.statesList.size(),
											mainList.isInitial(mainList.statesList.size()),
											false, 
											Character.toString(expression.charAt(i)), 
											nextState
											);
						
						newState.setChainStart(true);
						mainList.statesList.add(newState);
						
						for (int j = mainList.statesList.size()-1; j >= 0; j--) {
							if(mainList.statesList.get(j).isUnionStart()) {
								nextState = new State(mainList.statesList.size()-1,
														false,
														mainList.isFinal(i, expression.length())
														);
								
								mainList.statesList.get(j).addItemtoChange("L");
								mainList.statesList.get(j).addNextState(nextState);
								
								j = -1;
							}
						}
						
					} else {
						nextState = new State(mainList.statesList.size()+1,
											false,
											mainList.isFinal(i, expression.length())
											);
						
						newState = new State(mainList.statesList.size(),
											mainList.isInitial(mainList.statesList.size()),
											false, 
											Character.toString(expression.charAt(i)), 
											nextState
											);
						
						mainList.statesList.add(newState);
						
						for (int j = mainList.statesList.size()-1; j >= 0; j--) {
							if(mainList.statesList.get(j).isUnionStart()) {
								nextState = new State(mainList.statesList.size()-1,
														false,
														mainList.isFinal(i, expression.length())
														);
								
								mainList.statesList.get(j).addItemtoChange("L");
								mainList.statesList.get(j).addNextState(nextState);
								
								j = -1;
							}
						}
						
						int unionEnd = -1;
						for (int j = mainList.statesList.size()-1; j >= 0; j--) {
							if(mainList.statesList.get(j).isUnionEnd()) {
								unionEnd = j;
								j = -1;
							}
						}
						
						nextState = new State(mainList.statesList.get(unionEnd).getName(),
											false,
											mainList.isFinal(i, expression.length())
											);
						
						newState = new State(mainList.statesList.size(),
											mainList.isInitial(mainList.statesList.size()),
											false, 
											"L", 
											nextState
											);
						
						mainList.statesList.add(newState);
					}
					
				}
				
				else if(mainList.statesList.size() > 0) {
					boolean hasUnion = false;
					for (int j = mainList.statesList.size()-1; j >= 0; j--) {
						if (mainList.statesList.get(j).isUnionEnd()) {
							
							mainList.statesList.get(j).setNextStateName(0, mainList.statesList.size());
							mainList.statesList.get(j).getItemToChange().remove(0);
							mainList.statesList.get(j).getItemToChange().add(Character.toString(expression.charAt(i)));
							mainList.statesList.get(j).setUnionEnd(false);
							
							hasUnion = true;
							j = -1;
						}
					}
					
					if(!hasUnion) {
						nextState = new State(mainList.statesList.size()+1,
											false,
											mainList.isFinal(i, expression.length())
											);
						
						newState = new State(mainList.statesList.size(),
											mainList.isInitial(mainList.statesList.size()),
											false,
											Character.toString(expression.charAt(i)), 
											nextState
											);
						
						if(reader.validNext(i, expression.length())) {
							if(expression.charAt(i+1) == '*' || reader.isReservedWord(expression.charAt(i+1))) {
								newState.setChainStart(true);
							}
						}
						
						mainList.statesList.add(newState);
					}
					
				}
				
				
				
			}
			
			else if(expression.charAt(i) == '*') {
				mainList = mainList.foundStarKey(mainList, i, expression);
				if (reader.validPreview(i-1) && expression.charAt(i-2) == 'U') {
					int unionEnd = -1;
					for (int j = mainList.statesList.size()-1; j >= 0; j--) {
						if(mainList.statesList.get(j).isUnionEnd()) {
							unionEnd = j;
							j = -1;
						}
					}
					
					nextState = new State(mainList.statesList.get(unionEnd).getName(),
										false,
										mainList.isFinal(i, expression.length())
										);
					
					mainList.statesList.get(mainList.statesList.size()-1).addItemtoChange("L");
					mainList.statesList.get(mainList.statesList.size()-1).addNextState(nextState);;
				}
				//mainList.statesList.get(mainList.getLastState()).setChainEnd(true);
			}
			
			else if(expression.charAt(i) == 'U') {
				mainList = mainList.foundUnion(mainList, i, expression);
			}
			
			else if(expression.charAt(i) == '('){
				
				
				ArrayList<State> auxStateList = new ArrayList<State>();
				ConversionER auxiliar = new ConversionER();
				
				/* O Javinha est� de brincadeira com a nossa cara - ele est� salvando TODO O ROLE na porra do mainList que eu chamei S� PRA PASSAR PRO TRANSF */
				/* Como a l�gica nunca � realmente l�gica - vamos deixar assim - caso de BUG NO MAINLIST volte aqui (: - Oremos. */
				auxStateList = mainList.logicFunction(auxiliar, expression.substring(i+1, expression.length())).statesList;
				
				if(mainList.statesList.size() > 0) {
					/* Improv�veis - Jogo do troca - VALENDO! */
					boolean oldLinksNew = false;
					for (int j = 0; j < auxStateList.size(); j++) {
						auxStateList.get(j).setName(mainList.statesList.size()+j);
						for (int j2 = 0; j2 < auxStateList.get(j).getNextState().size(); j2++) {
							auxStateList.get(j).setNextStateName(j2, (auxStateList.get(j).getNextStateName(j2))+(mainList.statesList.size()));
						}
					}
					
					for (int j = 0; j < mainList.statesList.get(mainList.statesList.size()-1).getNextState().size(); j++) {
						for (int j2 = 0; j2 < auxStateList.size(); j2++) {
							if(mainList.statesList.get(mainList.statesList.size()-1).getNextState().get(j).getName() == auxStateList.get(j2).getName()) {
								oldLinksNew = true;
							}
						}
					}
					
					if(!oldLinksNew) {
						
						for (int j = 0; j < auxStateList.get(0).getNextState().size(); j++) {
							mainList.statesList.get(mainList.statesList.size()-1).addItemtoChange(auxStateList.get(0).getItemToChange().get(j));
							mainList.statesList.get(mainList.statesList.size()-1).addNextState(auxStateList.get(0).getNextState().get(j));
						}
						
						for (int k = 0; k < mainList.statesList.get(mainList.statesList.size()-1).getNextState().size(); k++) {
							for (int j2 = 0; j2 < auxStateList.get(0).getNextState().size(); j2++) {
								if(mainList.statesList.get(mainList.statesList.size()-1).getNextStateName(k) == auxStateList.get(0).getNextStateName(j2)) {
									mainList.statesList.get(mainList.statesList.size()-1).setNextStateName(k, mainList.statesList.get(mainList.statesList.size()-1).getName()+1);
								}
							}
						}
						
						auxStateList.remove(0);
					}
				}
				
				mainList.statesList.addAll(auxStateList);
				
				System.out.println();
				System.out.println();
				for (int j = 0; j < mainList.statesList.size(); j++) {
					if (mainList.statesList.get(j).isChainStart()) {
						System.out.println("ESSE EMBAIXO E START!");
					}  if (mainList.statesList.get(j).isChainEnd()) {
						System.out.println("ESSE EMBAIXO E END!");
					}  if (mainList.statesList.get(j).isUnionStart()) {
						System.out.println("ESSE EMBAIXO E UNION START!");
					}  if (mainList.statesList.get(j).isUnionEnd()) {
						System.out.println("ESSE EMBAIXO E UNION END!");
					}
					mainList.statesList.get(j).printState();
				}
				System.out.println();System.out.println();

				for (int k = i+1; k < expression.length(); k++) {
					if (expression.charAt(k) == ')') {
						i = k;
						k = expression.length();
						if (reader.validNext(k, expression.length()) && expression.charAt(k+1) == 'U') {
							
						}
					}
				}
			}
			
			else if(expression.charAt(i) == ')'){
				
				if (mainList.statesList.size() > 0 && mainList.statesList.get(0).isUnionStart()) {

					int startPosition = mainList.statesList.size()-1;
					int endPosition = 0;
					
					for (int j = 0; j < mainList.statesList.size(); j++) {
						if (mainList.statesList.get(j).isChainStart() && (mainList.statesList.get(j).getName() < mainList.statesList.get(startPosition).getName())) {
							startPosition = j;
						}
						
						else if(mainList.statesList.get(j).isChainStart()){
							mainList.statesList.get(j).setChainStart(false);
						}
						
						else if(mainList.statesList.get(j).isUnionEnd() && (mainList.statesList.get(j).getName() > mainList.statesList.get(endPosition).getName())) {
							endPosition = j;
						}
						
						else if(mainList.statesList.get(j).isUnionEnd()){
							mainList.statesList.get(j).setChainEnd(false);
						}
						
						else if(mainList.statesList.get(j).isChainEnd()) {
							mainList.statesList.get(j).setChainEnd(false);
						}
					}
					
					if (startPosition > 0 && startPosition != mainList.statesList.size()) {
						mainList.statesList.get(startPosition-1).setChainStart(true);
						mainList.statesList.get(startPosition).setChainStart(false);
					}
					
					if (endPosition > 0) {
						mainList.statesList.get(endPosition).setChainEnd(true);
					}
					
					mainList.statesList.get(mainList.statesList.size()-1).setInsideUnion(true);
				}
				
				return mainList;
			}
			
			else {
				System.out.println("Erro na cria��o do estado");
			}
		}
		
		return mainList;
	}
	
	public ConversionER foundUnion(ConversionER conversion, int i, String expression) {
		
		State nextState = new State();
		State newState = new State();
		
		/* Primeira parte da Uni�o 
		 * Cria o ultimo estado da uniao*/
		if (conversion.statesList.get(conversion.getLastState()).isInsideUnion()) {
			for (int j = conversion.statesList.size()-1; j > 0 ; j--) {
				if (conversion.statesList.get(j).isUnionEnd()) {
					
					
					nextState = new State(-1,
										false,
										conversion.isFinal(i, expression.length())
										);
					
					newState = new State(conversion.statesList.size(),
										conversion.isInitial(conversion.statesList.size()),
										false, 
										null, 
										nextState
										);
					newState.setUnionEnd(true);
					conversion.statesList.add(newState);
					
					System.out.println("VOU LINKAR O ESTADO q"+conversion.statesList.get(j).getName()+ " COM q" +newState.getName());
					newState.printState();
					/* REAL */
					conversion.statesList.get(j).getItemToChange().add("L");
					conversion.statesList.get(j).getNextState().add(newState);
					
					/* REMOVER O -1 E NULL DA LIGA��O */
					for (int j2 = 0; j2 < conversion.statesList.get(j).getNextState().size(); j2++) {
						if (conversion.statesList.get(j).getNextState().get(j2).getName() == -1 && conversion.statesList.get(j).getItemToChange().get(j2) == null) {
							System.out.println("ENTREI NO IF");
							conversion.statesList.get(j).getNextState().remove(j2);
							conversion.statesList.get(j).getItemToChange().remove(j2);
							j2 = conversion.statesList.size();
						}
					}
					/* REMOVER A �LTIMA TRANSI��O Q-1 - VOLTAR AQUI */
					/* REAL */
					conversion.statesList.get(j).printState();
					System.out.println("TERMINEI O IF * DENTRO DO U");
					
					j = 0;
				}
			}
		}
		else if (conversion.statesList.get(conversion.getLastState()).isChainEnd()) {
			
			nextState = new State(-1,
								false,
								conversion.isFinal(i, expression.length())
								);
			
			newState = new State(conversion.statesList.size(),
								conversion.isInitial(conversion.statesList.size()),
								false, 
								null, 
								nextState
								);
			newState.setUnionEnd(true);
			conversion.statesList.add(newState);
			
			System.out.println("VOU LINKAR O ESTADO q"+conversion.statesList.get(conversion.statesList.size()-2).getName()+ " COM q"+newState.getName());
			newState.printState();
			/* REAL */
			conversion.statesList.get(conversion.statesList.size()-2).getItemToChange().add("L");
			conversion.statesList.get(conversion.statesList.size()-2).getNextState().add(newState);
			/* REAL */
			conversion.statesList.get(conversion.statesList.size()-2).printState();
			System.out.println("TERMINEI O IF * DENTRO DO U");
		
		}else {
			nextState = new State(conversion.statesList.size()+1,
								false,
								conversion.isFinal(i, expression.length())
								);
			
			newState = new State(conversion.statesList.size(),
								conversion.isInitial(conversion.statesList.size()),
								false, 
								"L", 
								nextState
								);
			newState.setUnionEnd(true);
			conversion.statesList.add(newState);
		}
		
		/* Segunda parte da Uni�o */
		/* Acha a posi��o elemento inicial da cadeia que ser� afetada pelo Uni�o */
		int startChainPosition = -1;
		for (int j = conversion.statesList.size()-1; j >= 0 ; j--) {
			if(conversion.statesList.get(j).isChainStart()) {
				startChainPosition = j;
				j = -1;
				/* Achou a posi��o elemento inicial da cadeia que ser� afetada pela Uni�o */
			}
		}

		/* Terceira parte do Uni�o */
		ArrayList<State> auxStateList = new ArrayList<State>();
		
		nextState = new State(conversion.statesList.get(startChainPosition+1).getName(),
						conversion.isInitial(conversion.statesList.size()),
						conversion.isFinal(i, expression.length())
						);
		
		newState = new State(startChainPosition,
						conversion.isInitial(conversion.statesList.size()),
						conversion.isFinal(i, expression.length()),
						"L", 
						nextState
						);
		newState.setUnionStart(true);
		auxStateList.add(newState);
		
		
		/* PASSSO 6 - Recorto do original pro auxiliar */
		boolean control = true;
		while(control) {
			
			if(startChainPosition == conversion.statesList.size()) {
				control = false;
			}else {
				auxStateList.add(conversion.statesList.get(startChainPosition));
				conversion.statesList.remove(startChainPosition);
			}
		}
		
		/* PASSO 7 - Troco os nomes */

		for (int j = 1; j < auxStateList.size(); j++) { /* Verificar o limitador */
			auxStateList.get(j).setName(auxStateList.get(j).getName()+1);
		}
		
		for (int j = 1; j < auxStateList.size(); j++) {
			for (int j2 = 0; j2 < auxStateList.get(j).getNextState().size(); j2++) {
				if (auxStateList.get(j).getNextState().get(j2).getName() >= 0 && !auxStateList.get(j).getNextState().get(j2).isUnionEnd() ) {
					auxStateList.get(j).setNextStateName(j2, (auxStateList.get(j).getNextStateName(j2))+1);
				}
			}
		}
		
		/* PASSO 8 - Acho fim da uni�o e adiciono o next state */
		for (int j = auxStateList.size()-1; j >= 0; j--) {
			if (auxStateList.get(j).isUnionEnd()) {
				if (auxStateList.get(j-1).isChainEnd()) {
					j = -1;
				}else {
					nextState = new State(-1,
										false,
										conversion.isFinal(i, expression.length())
										);
					
					newState = new State(auxStateList.get(j).getNextStateName(0),
										conversion.isInitial(conversion.statesList.size()),
										false, 
										null, 
										nextState
										);
					
					auxStateList.get(j).setUnionEnd(false);
					newState.setUnionEnd(true);
					auxStateList.add(newState);
					j = -1;
				}
			}
		}
		
		/* Troca das marca��es pras pr�ximas l�gicas */
		auxStateList.get(0).setChainStart(true);
		auxStateList.get(1).setChainStart(false);
		auxStateList.get(auxStateList.size()-1).setChainEnd(false);
		auxStateList.get(auxStateList.size()-2).setChainEnd(false);
		
		/* Adi��o do vetor auxiliar no original */
		conversion.statesList.addAll(auxStateList);
		return conversion;
	}
	
	public ConversionER foundStarKey(ConversionER conversion, int i, String expression){
		State nextState = new State();
		State newState = new State();
		
		/* Primeira parte do fecho de Kleene */
		nextState = new State(conversion.statesList.size()+1,
							false,
							conversion.isFinal(i, expression.length())
							);
		
		newState = new State(conversion.statesList.size(),
							conversion.isInitial(conversion.statesList.size()),
							false, 
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
		
		nextState = new State(conversion.statesList.get(startChainPosition).getName(),
							conversion.isInitial(conversion.statesList.size()),
							conversion.isFinal(i, expression.length())
							);
		
		newState = new State(conversion.statesList.size(),
							false,
							conversion.isFinal(i, expression.length()),
							"L", 
							nextState
							);
		
		conversion.statesList.add(newState);
		
		/* Terceira parte do fecho de Kleene */
		ArrayList<State> auxStateList = new ArrayList<State>();
		
		nextState = new State(conversion.statesList.get(startChainPosition+1).getName(),
							conversion.isInitial(conversion.statesList.size()),
							conversion.isFinal(i, expression.length())
							);
		
		newState = new State(startChainPosition,
							conversion.isInitial(conversion.statesList.size()),
							conversion.isFinal(i, expression.length()),
							"L", 
							nextState
							);
		
		newState.setChainStart(true);
		auxStateList.add(newState);
		
		/* Recorto do original pro auxiliar */
		nextState = new State(conversion.statesList.get(startChainPosition+1).getName(),
							conversion.isInitial(conversion.statesList.size()),
							conversion.isFinal(i, expression.length())
							);
		
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
		nextState = new State(auxStateList.get(1).getName(),
							conversion.isInitial(conversion.statesList.size()),
							conversion.isFinal(i, expression.length())
							);
		
		newState = new State(auxStateList.size()+conversion.statesList.size(),
							false,
							conversion.isFinal(i, expression.length()),
							"L",
							nextState
							);
		auxStateList.add(newState);
		
		/* Adi��o da nova transi��o do novo incial pro novo final do vetor aux */
		
		auxStateList.get(0).addNextState(new State(auxStateList.get(auxStateList.size()-1).getName(),
													conversion.isInitial(conversion.statesList.size()),
													conversion.isFinal(i, expression.length())
													));
		auxStateList.get(0).addItemtoChange("L");
		
		/* Realizo as marca��es para controle de l�gica do inicio e fim do Kleene */
		auxStateList.get(0).setChainStart(true);
		auxStateList.get(1).setChainStart(false);
		auxStateList.get(auxStateList.size()-1).setChainEnd(true);
		
		/* Adi��o do vetor auxiliar no original */
		conversion.statesList.addAll(auxStateList);
		return conversion;
	}
	 
	/* mudar para q0 */
	public boolean isInitial(int size) {
		if(size == 0) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isFinal(int position, int size) {
		if(position == size-1) {
			return true;
		}else {
			return false;
		}
	}
	
	public int getLastState() {
		return this.statesList.size()-1;
	}
}
