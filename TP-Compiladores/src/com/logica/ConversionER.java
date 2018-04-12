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
					
					conversion.statesList.add(newState);
					
				}else if(expression.charAt(i-1) == '*') {
					System.out.println("Entrei no if");
					nextState = new State(conversion.statesList.size(),
											false,
											conversion.isFinal(i, expression.length())
											);
					conversion.statesList.get(conversion.statesList.size()-1).addItemtoChange(Character.toString(expression.charAt(i)));
					conversion.statesList.get(conversion.statesList.size()-1).addNextState(nextState);
				}else if(conversion.statesList.size() > 0) {
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
					
					conversion.statesList.add(newState);
				}
				
				
				if(reader.validNext(i, expression.length())) {
					if(expression.charAt(i+1) == '*' || reader.isReservedWord(expression.charAt(i+1))) {
						newState.setChainStart(true);
					}
				}
				
				
			}
			
			else if(expression.charAt(i) == '*') {
				/* Primeira parte do fecho de Kleene */
				nextState = new State((i+1),
									false,
									conversion.isFinal(i, expression.length())
									);
				
				newState = new State(i,
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
						/* Achou a posição elemento inicial da cadeia que será afetada por Kleene */
					}
					
				}
				
				nextState = new State(conversion.statesList.get(startChainPosition).getName(),
									conversion.isInitial(conversion.statesList.size()),
									conversion.isFinal(i, expression.length())
									);
				
				newState = new State(i+1,
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
				
				for (int j = 1; j < auxStateList.size(); j++) {
					auxStateList.get(j).setName(auxStateList.get(j).getName()+1);
					for (int j2 = 0; j2 < auxStateList.get(j).getNextState().size(); j2++) {
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
