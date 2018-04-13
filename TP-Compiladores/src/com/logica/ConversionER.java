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
		
		/* Talvez dê pala com mais parênteses */
		conversion.statesList = new ArrayList<State>();
		conversion = aux.logicFunction(aux, expression);

		System.out.println();
		for (int i = 0; i < conversion.statesList.size(); i++) {
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
					
					mainList.statesList.add(newState);
				}
				
				else if(expression.charAt(i-1) == '*') {
					nextState = new State(mainList.statesList.size(),
											false,
											mainList.isFinal(i, expression.length())
											);
					mainList.statesList.get(mainList.statesList.size()-1).addItemtoChange(Character.toString(expression.charAt(i)));
					mainList.statesList.get(mainList.statesList.size()-1).addNextState(nextState);
					
					if(reader.validNext(i, expression.length())) {
						if(expression.charAt(i+1) == '*' || reader.isReservedWord(expression.charAt(i+1))) {
							mainList.statesList.get(mainList.statesList.size()-1).setChainStart(true);
						}
					}
					
				}
				
				else if(expression.charAt(i-1) == 'U') {
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
			}
			
			else if(expression.charAt(i) == 'U') {
				mainList = mainList.foundUnion(mainList, i, expression);
			}
			
			else if(expression.charAt(i) == '('){
				
				
				ArrayList<State> transf = new ArrayList<State>();
				
				/* O Javinha está de brincadeira com a nossa cara - ele está salvando TODO O ROLE na porra do mainList que eu chamei SÓ PRA PASSAR PRO TRANSF */
				/* Como a lógica nunca é realmente lógica - vamos deixar assim - caso de BUG NO MAINLIST volte aqui (: - Oremos. */
				transf = mainList.logicFunction(mainList, expression.substring(i+1, expression.length())).statesList;
					
				//mainList.statesList.addAll(transf);

				for (int k = i+1; k < expression.length(); k++) {
					if (expression.charAt(k) == ')') {
						i = k;
						k = expression.length();
					}
				}
			}
			
			else if(expression.charAt(i) == ')'){
				return mainList;
			}
			
			else {
				System.out.println("Erro na criação do estado");
			}
		}
		
		return mainList;
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
