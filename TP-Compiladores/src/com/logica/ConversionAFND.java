package com.logica;

import java.util.ArrayList;
import java.util.Scanner;

public class ConversionAFND {
	public ConversionER conversionER = new ConversionER();
	/* Arraylist estiloso para salvar todo o processo */
	public ArrayList<String> weber = new ArrayList<String>();
	
	public static void main(String[] args) {
		
		ConversionER conversion = new ConversionER();
		ConversionER aux = new ConversionER();
		ConversionAFND conversionAFND = new ConversionAFND();
		Read reader = new Read();
		
		/* Teste de entrada via teclado */
		Scanner in = new Scanner(System.in);
		String expression = in.nextLine();
		
		if(reader.validSentence(expression)) {
			System.out.println("Senten�a V�lida");
			in.close();
			/* Fim Teste de entrada via teclado */

			conversion.statesList = new ArrayList<State>();
			conversion = aux.logicFunction(aux, expression);
			conversion.statesList = conversion.setInitialOrEnd(conversion.statesList);

			/*System.out.println();
			for (int i = 0; i < conversion.statesList.size(); i++) {
				
				/*if (conversion.statesList.get(i).isChainStart()) {System.out.println("ESSE EMBAIXO E CHAIN START!");} 
				if (conversion.statesList.get(i).isChainEnd()) {System.out.println("ESSE EMBAIXO E CHAIN END!");}  
				if (conversion.statesList.get(i).isUnionStart()) {System.out.println("ESSE EMBAIXO E UNION START!");}  
				if (conversion.statesList.get(i).isUnionEnd()) {System.out.println("ESSE EMBAIXO E UNION END!");} 	
				if (conversion.statesList.get(i).isInsideUnion()) {System.out.println("ESSE EMBAIXO E INSIDE UNION!");} 
				if (conversion.statesList.get(i).isLeftStart()) {System.out.println("ESSE EMBAIXO E POS PARENTESE ESQUERDO");}
				if (conversion.statesList.get(i).isRightEnd()) {System.out.println("ESSE EMBAIXO E PRE PARENTESE DIREITO");}*/
				
				/*conversion.statesList.get(i).printState();*/
			//}
			conversion.writeGraphviz(conversion);
			
			/************************************ COME�A A PARTIR DAQUI ************************************/
			/************************************ COME�A A PARTIR DAQUI ************************************/
			/************************************ COME�A A PARTIR DAQUI ************************************/
			
			System.out.println("=====================REMO��O TRANSI��O LAMBDA=====================");
			/*Percorre a lista de nextState de cada estado procurando uma transi��o Lambda e chama a fun��o 'removeLambda' */
			for(int i=0; i < conversion.statesList.size(); i++) {
				
				for(int j=0; j < conversion.statesList.get(i).getNextState().size();j++) {
					
					if (conversion.statesList.get(i).getItemToChange().get(j).equals("L")) {
						System.out.println("ACHEI UM LAMBDA EM Q" + i);
						conversion.statesList = conversionAFND.removeLambda(i,
																			j,
																			conversion.statesList.get(i).getNextStateName(j),
																			conversion.statesList);
						j = j-1;
					}

				}
				
			}
			for (int i = 0; i < conversion.statesList.size(); i++) {
				conversion.statesList.get(i).printState();
			}

			System.out.println("===================AFD=======================");
			conversion.statesList = conversionAFND.afn2afd(conversion.statesList);
			for (int i = 0; i < conversion.statesList.size(); i++) {
				conversion.statesList.get(i).printState();
			}
		}
		
		else {
			System.out.println("Senten�a Inv�lida");
		}

	}
	
	// Remove a transi��o lambda de um elemento trazendo as transi��es do elemento alcan�ado por ele
	public ArrayList<State> removeLambda(int i, int j, int passState, ArrayList<State> stateList){
		System.out.println("Entrei no m�todo");
		//Percorre a lista de nextState do elemento a passar suas transi��es
		for (int k = 0; k < stateList.get(passState).getNextState().size(); k++) {

			/*System.out.println(stateList.get(i).getNextState().get(j).getName());
			System.out.println(stateList.get(passState).getNextState().get(k).getName());
			System.out.println(stateList.get(i).getNextState().get(j).getName()!=stateList.get(passState).getNextState().get(k).getName());
			System.out.println(stateList.get(i).getItemToChange().get(j));
			System.out.println(stateList.get(passState).getItemToChange().get(k));
			System.out.println(!stateList.get(i).getItemToChange().get(j).equals(stateList.get(passState).getItemToChange().get(k)));*/
			
			System.out.println("q"+stateList.get(i).getNextState().get(j).getName() + " == q" + stateList.get(k).getName() + "?");
			System.out.println("O valor de k �: " + k);
			if (stateList.get(i).getNextState().get(j).getName() == stateList.get(k).getName()) {
				
				System.out.println("ENCONTREI UM ELEMENTO q"+ stateList.get(i).getNextState().get(j).getName() + "Que � igual a q" + stateList.get(k).getName());
				
				/* Buscar a lista de nextState do Elemento X selecionado */
				for (int l = 0; l < stateList.get(k).getNextState().size(); l++) {
					if (stateList.get(i).getNextState().get(j).getName() == stateList.get(k).getNextState().get(l).getName() &&
							stateList.get(i).getItemToChange().get(j).equals(stateList.get(k).getItemToChange().get(l))) {
						//Do nothing - Jo�o das Neves
					}
					else if(stateList.get(i).getNextState().get(j).getName() == stateList.get(k).getNextState().get(l).getName()) {
						stateList.get(i).getNextState().add(new State(stateList.get(k).getNextState().get(l).getName()));
						stateList.get(i).getItemToChange().add(stateList.get(k).getItemToChange().get(l));
					}
				}
				
			}
			/* Buscar o elemento com o nome do nextState J do elemento I selecionado*/
			/*for (int x = 0; x < stateList.size(); x++) {
				System.out.println("ENTREI NO FOR DO X");
				
				
			}*/
		}

		stateList.get(i).getNextState().remove(j);
		stateList.get(i).getItemToChange().remove(j);

		return stateList;

	}

	private ArrayList<State> afn2afd(ArrayList<State> afn) {
		ArrayList<State> afd = new ArrayList<State>();
		ArrayList<String> createdStates = new ArrayList<String>();
		afd.add(afn.get(0));
		//Percorre todo o afd 
		for (int i = 0; i < afd.size(); i++) {
			//Para cada letra do alfabeto cria um novo elemento para realizar a fuuuuuuus�o HA
			for (int j = weber.size()-1; j >=0 ; j--) {
				State newElement = new State();
				newElement.setItemToChange(new ArrayList<String>());
				newElement.setNextState(new ArrayList<State>());
				StringBuilder fusionStateName = new StringBuilder();
				ArrayList<Integer> fusionStateNexts = new ArrayList<Integer>();
				for (int k = 0; k < afd.get(i).getNextState().size(); k++) {
					//Pega um estado por vez, juntando todos os estados que ele alcan�a lendo uma vari�vel do alfabeto e se chega
					//ao estado final '-1'
					if (afd.get(i).getItemToChange().get(k).equals(weber.get(j)) ) {
						fusionStateName.append(afd.get(i).getNextState().get(k).getName());
						fusionStateNexts.add(afd.get(i).getNextState().get(k).getName());

					}
				}
				if(!fusionStateNexts.isEmpty()) {
					if (!createdStates.contains(fusionStateName.toString())) {
						//adiciona o estado em uma lista de estados criados para futuras compara��es e evitar duplica��es
						createdStates.add(fusionStateName.toString());
						//cria um novo estado com o nome de todos os que fundiram
						newElement.setName(Integer.valueOf(fusionStateName.toString()));
						//Verifica se a lista de fusionStateNexts n�o est� vazia para n�o quebrar o for em baixo

						//Percorrer a lista de fusionStateNexts para copiar o nextState de todos
						for (int y = 0; y < fusionStateNexts.size(); y++) {
							//verifica se n�o est� com o '-1' do estado final
							if (fusionStateNexts.get(y)>=0) {
								newElement.getItemToChange().addAll(afn.get(fusionStateNexts.get(y)).getItemToChange());
								newElement.getNextState().addAll(afn.get(fusionStateNexts.get(y)).getNextState());
							}
						}
						afd.add(newElement);
					}

				}

			}
		}

		return afd;
	}
	
	private void saveLetter(char analfabeto) {
		if (weber.isEmpty()) {
			weber.add(String.valueOf(analfabeto));
		}
		else {
			boolean addChar = true;
			for (int i = 0; i < weber.size(); i++) {
				if (weber.get(i).equals(String.valueOf(analfabeto))) {
					addChar = false;
				}
			}
			if (addChar) {
				weber.add(String.valueOf(analfabeto));
			}
		}
	}
}
