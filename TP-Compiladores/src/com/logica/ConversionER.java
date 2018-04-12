package com.logica;

import java.util.ArrayList;
import java.util.Scanner;

public class ConversionER {
	/* Atributos */
	public ArrayList<State> statesList = new ArrayList<State>();
	public String expression;
	
	public static void main(String[] args) {
		ConversionER conversion = new ConversionER();
		
		State nextState = new State();
		State newState = new State();
		Read reader = new Read();
		
		/* Teste de entrada via teclado */
		Scanner in = new Scanner(System.in);
		String expression = in.nextLine();
		in.close();
		/* Fim Teste de entrada via teclado */
		
		for (int i = 0; i < expression.length(); i++) {
			if (reader.isLetterLower(expression.charAt(i))) {
				if (conversion.statesList.size() == 0) {
					nextState = new State((i+1),
										false,
										conversion.isFinal(i, expression.length())
										);
					
					newState = new State(i,
										conversion.isInitial(conversion.statesList.size()),
										false, 
										Character.toString(expression.charAt(i)), 
										nextState
										);
					
					if(reader.validNext(i, expression.length())) {
						if(expression.charAt(i+1) == '*' || reader.isReservedWord(expression.charAt(i+1))) {
							newState.setChainStart(true);
						}
					}
					
					conversion.statesList.add(newState);
					
				}else if(expression.charAt(i-1) == '*') {
					nextState = new State(conversion.statesList.size(),
											false,
											conversion.isFinal(i, expression.length())
											);
					conversion.statesList.get(conversion.statesList.size()-1).addItemtoChange(Character.toString(expression.charAt(i)));
					conversion.statesList.get(conversion.statesList.size()-1).addNextState(nextState);
					
					if(reader.validNext(i, expression.length())) {
						if(expression.charAt(i+1) == '*' || reader.isReservedWord(expression.charAt(i+1))) {
							conversion.statesList.get(conversion.statesList.size()-1).setChainStart(true);
						}
					}
					
				}
				
				else if(expression.charAt(i-1) == 'U') {
					nextState = new State(conversion.statesList.size()+2,
										false,
										conversion.isFinal(i, expression.length())
										);
					
					newState = new State(conversion.statesList.size()+1,
										conversion.isInitial(conversion.statesList.size()),
										false, 
										Character.toString(expression.charAt(i)), 
										nextState
										);
					
					conversion.statesList.add(newState);
					
					for (int j = conversion.statesList.size()-1; j >= 0; j--) {
						if(conversion.statesList.get(j).isUnionStart()) {
							nextState = new State(conversion.statesList.size(),
										false,
										conversion.isFinal(i, expression.length())
										);
							
							conversion.statesList.get(j).addItemtoChange("L");
							conversion.statesList.get(j).addNextState(nextState);
							
							j = -1;
						}
					}
					
					nextState = new State(conversion.statesList.size()-1,
										false,
										conversion.isFinal(i, expression.length())
										);
					
					newState = new State(conversion.statesList.size()+1,
										conversion.isInitial(conversion.statesList.size()),
										false, 
										"L", 
										nextState
										);
					
					conversion.statesList.add(newState);
				}
				
				else if(conversion.statesList.size() > 0) {
					boolean hasUnion = false;
					for (int j = conversion.statesList.size()-1; j >= 0; j--) {
						if (conversion.statesList.get(j).isUnionEnd()) {
							
							conversion.statesList.get(j).setNextStateName(0, conversion.statesList.size());
							conversion.statesList.get(j).getItemToChange().remove(0);
							conversion.statesList.get(j).getItemToChange().add(Character.toString(expression.charAt(i)));
							
							conversion.statesList.get(j).printState();
							conversion.statesList.get(conversion.statesList.size()-1).printState();
							hasUnion = true;
							j = -1;
						}
					}
					
					if(hasUnion == false) {
						nextState = new State(conversion.statesList.size()+1,
											false,
											conversion.isFinal(i, expression.length())
											);
						
						newState = new State(conversion.statesList.size(),
											conversion.isInitial(conversion.statesList.size()),
											false, 
											Character.toString(expression.charAt(i)), 
											nextState
											);
						
						if(reader.validNext(i, expression.length())) {
							if(expression.charAt(i+1) == '*' || reader.isReservedWord(expression.charAt(i+1))) {
								newState.setChainStart(true);
							}
						}
						
						conversion.statesList.add(newState);
					}
					
				}
				
				
				
			}
			
			else if(expression.charAt(i) == '*') {
				conversion = conversion.foundStarKey(conversion, i, expression);
			}
			
			else if(expression.charAt(i) == 'U') {
				conversion = conversion.foundUnion(conversion, i, expression);
				conversion.statesList.get(conversion.statesList.size()-1).printState();
			}
			
			else {
				System.out.println("Erro na criação do estado");
			}
		}
		System.out.println();
		for (int i = 0; i < conversion.statesList.size(); i++) {
			conversion.statesList.get(i).printState();
		}
	}
	
	public ConversionER foundUnion(ConversionER conversion, int i, String expression) {
		
		State nextState = new State();
		State newState = new State();
		
		/* Primeira parte da União */
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
		
		/* Segunda parte da União */
		/* Acha a posição elemento inicial da cadeia que será afetada pelo União */
		int startChainPosition = 0;
		for (int j = conversion.statesList.size()-1; j >= 0 ; j--) {
			if(conversion.statesList.get(j).isChainStart()) {
				startChainPosition = j;
				j = -1;
				/* Achou a posição elemento inicial da cadeia que será afetada pela União */
			}
		}

		/* Terceira parte do União */
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
		
		
		/* Recorto do original pro auxiliar */
		boolean control = true;
		while(control) {
			
			if(startChainPosition == conversion.statesList.size()) {
				control = false;
			}else {
				auxStateList.add(conversion.statesList.get(startChainPosition));
				conversion.statesList.remove(startChainPosition);
			}
		}
		
		/* Troco os nomes */
		for (int j = 1; j < auxStateList.size(); j++) {
			auxStateList.get(j).setName(auxStateList.get(j).getName()+1);
			for (int j2 = 0; j2 < auxStateList.get(j).getNextState().size(); j2++) {
				auxStateList.get(j).setNextStateName(j2, (auxStateList.get(j).getNextStateName(j2))+1);
			}
		}
		
		for (int j = auxStateList.size()-1; j >= 0; j--) {
			if (auxStateList.get(j).isUnionEnd()) {
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
		/* Adição do vetor auxiliar no original */
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
		/* Acha a posição elemento inicial da cadeia que será afetada por Kleene */
		int startChainPosition = 0;
		for (int j = conversion.statesList.size()-1; j >= 0 ; j--) {
			if(conversion.statesList.get(j).isChainStart()) {
				startChainPosition = j;
				j = -1;
				/* Achou a posição elemento inicial da cadeia que será afetada por Kleene */
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
		/* Adição o último estado do vetor aux apontando pro antigo inicial */
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
		
		/* Adição da nova transição do novo incial pro novo final do vetor aux */
		
		auxStateList.get(0).addNextState(new State(auxStateList.get(auxStateList.size()-1).getName(),
													conversion.isInitial(conversion.statesList.size()),
													conversion.isFinal(i, expression.length())
													));
		auxStateList.get(0).addItemtoChange("L");
		
		/* Adição do vetor auxiliar no original */
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
	
}
