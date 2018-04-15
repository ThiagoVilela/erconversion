package com.logica;

import java.util.ArrayList;

public class State {
	/* Atributos */
	private int name;
	private boolean isInicial;
	private boolean isFinal;
	private ArrayList<String> transition = new ArrayList<String>();
	private ArrayList<State> nextState = new ArrayList<State>();
	
	private boolean chainStart = false;
	private boolean chainEnd = false;

	private boolean unionStart = false;
	private boolean unionEnd = false;
	
	private boolean insideUnion = false;

	/* Construtores */
	public State() {
		super();
		this.name = -1;
		this.isInicial = false;
		this.isFinal = false;
		this.transition = null;
		this.nextState = null;
		this.chainStart = false;
	}
	
	public State(int name) {
		super();
		this.name = name;
		this.isInicial = false;
		this.isFinal = false;
		this.transition = null;
		this.nextState = null;
		this.chainStart = false;
	}
	
	public State(int name, boolean isInicial) {
		super();
		this.name = name;
		this.isInicial = isInicial;
		this.isFinal = false;
		this.transition = null;
		this.nextState = null;
		this.chainStart = false;
	}
	
	public State(int name, boolean isInicial, boolean isFinal) {
		super();
		this.name = name;
		this.isInicial = isInicial;
		this.isFinal = isFinal;
		this.transition = null;
		this.nextState = null;
		this.chainStart = false;
	}
	
	public State(int name, boolean isInicial, boolean isFinal, String newTransition,
			State newNextState) {
		super();
		this.name = name;
		this.isInicial = isInicial;
		this.isFinal = isFinal;
		this.transition.add(newTransition) ;
		this.nextState.add(newNextState);
		this.chainStart = false;
	}
	
	public State(int name, boolean isInicial, boolean isFinal, String newTransition,
			State newNextState, boolean chainStart) {
		super();
		this.name = name;
		this.isInicial = isInicial;
		this.isFinal = isFinal;
		this.transition.add(newTransition) ;
		this.nextState.add(newNextState);
		this.chainStart = chainStart;
	}
	
	public State(int name, boolean isInicial, boolean isFinal, ArrayList<String> transition,
			ArrayList<State> nextState) {
		super();
		this.name = name;
		this.isInicial = isInicial;
		this.isFinal = isFinal;
		this.transition = transition;
		this.nextState = nextState;
		this.chainStart = false;
	}
	
	/* Printa bonitinho o estado */
	public void printState() {
		System.out.println("q"+this.getName());
		if (this.isInicial) {
			System.out.println("Inicial");
		}
		
		if (this.isFinal) {
			System.out.println("Final");
		}
		for (int i = 0; i < this.getItemToChange().size(); i++) {
			System.out.println("q"+this.getName() + " -" + this.getItemToChange().get(i) + "> " + "q"+this.getNextState().get(i).getName());
		}
	}
	
	/* Métodos Get'n Set */
	public int getName() {
		return name;
	}
	public void setName(int name) {
		this.name = name;
	}
	public boolean isInicial() {
		return isInicial;
	}
	public void setInicial(boolean isInicial) {
		this.isInicial = isInicial;
	}
	public boolean isFinal() {
		return isFinal;
	}
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}
	public ArrayList<String> getItemToChange() {
		return transition;
	}
	public void addItemtoChange(String item) {
		this.getItemToChange().add(item);
	}
	public ArrayList<String> getTransition() {
		return transition;
	}

	public void setTransition(ArrayList<String> transition) {
		this.transition = transition;
	}

	public void setItemToChange(ArrayList<String> itemToChange) {
		this.transition = itemToChange;
	}
	public ArrayList<State> getNextState() {
		return nextState;
	}
	public void addNextState(State newNextState) {
		this.nextState.add(newNextState);
	}
	public void setNextState(ArrayList<State> nextState) {
		this.nextState = nextState;
	}
	public int getNextStateName(int position) {
		return this.nextState.get(position).getName();
	}
	public void setNextStateName(int position, int name) {
		this.nextState.get(position).setName(name);
	}
	public boolean isChainStart() {
		return chainStart;
	}
	public void setChainStart(boolean chainStart) {
		this.chainStart = chainStart;
	}
	public boolean isUnionStart() {
		return unionStart;
	}
	public void setUnionStart(boolean unionStart) {
		this.unionStart = unionStart;
	}
	public boolean isUnionEnd() {
		return unionEnd;
	}
	public void setUnionEnd(boolean unionEnd) {
		this.unionEnd = unionEnd;
	}
	public boolean isChainEnd() {
		return chainEnd;
	}
	public void setChainEnd(boolean chainEnd) {
		this.chainEnd = chainEnd;
	}
	public boolean isInsideUnion() {
		return insideUnion;
	}
	public void setInsideUnion(boolean insideUnion) {
		this.insideUnion = insideUnion;
	}
}
