package com.logica;

import java.util.Scanner;

public class Read {
	public static void main(String[] args) {
		String entrada = new String();
		Read objeto = new Read();
		
		/* Teste de entrada via teclado */
		Scanner in = new Scanner(System.in);
		entrada = in.nextLine();
		in.close();
		/* Fim Teste de entrada via teclado */
		
		if(objeto.validSentence(entrada)) {
			System.out.println("Senten�a V�lida");
		}else {
			System.out.println("Senten�a Inv�lida");
		}
		
	}
	
	/* M�todos */
	boolean isLetterLower(char item) {
		return (Character.isLetter(item) && Character.isLowerCase(item)) ? true : false;
	}
	
	boolean isLetterUpper(char item) {
		return (Character.isLetter(item) && Character.isUpperCase(item)) ? true : false;
	}
	
	boolean isReservedWord(char item) {
		return (item == 'U' || item == 'I' || item == 'L') ? true : false;
	}
	
	boolean validPreview(int position) {
		return (position-1 >= 0)  ? true : false;
	}
	
	boolean validNext(int position, int size) {
		return (position+1 < size) ? true : false;
	}
	
	boolean validSentence(String entrada) {
		int parenthesisStack = 0;
		
		/* Verificar mais casos de uso para comentar os SYSOUT */
		for (int i = 0; i < entrada.length(); i++) {

			if(this.isLetterLower(entrada.charAt(i))) {
				System.out.println("Show - Letra Min�scula");
			}
			
			else if(this.isLetterUpper(entrada.charAt(i))) {
				if(this.isReservedWord(entrada.charAt(i))) {
					if(this.validPreview(i) && this.validNext(i, entrada.length())) {
						if((this.isLetterLower(entrada.charAt(i-1)) || (entrada.charAt(i-1) == ')') || (entrada.charAt(i-1) == '*')) 
								&& ((this.isLetterLower(entrada.charAt(i+1))) || (entrada.charAt(i+1) == '('))) {
							System.out.println("Show - Letra Mai�scula");
						}else {
							System.out.println("Erro - Letra Mai�scula (anterior ou posterior inv�lido)");
							return false;
						}
					} else {
						System.out.println("Erro - letra mai�scula (n�o tem anterior ou posterior)");
						return false;
					}
					//System.out.println("Show nos sinais reservados");
				}else {
					System.out.println("Erro - letra mai�scula (n�o � reservada)");
					return false;
				}
			}
			
			else if(entrada.charAt(i) == '(') {
				parenthesisStack++;
			}
			
			else if(entrada.charAt(i) == ')') {
				if (parenthesisStack-1 < 0) {
					System.out.println("Erro - Parentese Direito");
					return false;
				}
				else {
					parenthesisStack--;
				}
			}
			
			else if(entrada.charAt(i) == '*') {
				if(this.validPreview(i)) {
					if((this.isLetterLower(entrada.charAt(i-1))) || (entrada.charAt(i-1) == ')')) {
						System.out.println("Show - Klene!");
					}else {
						System.out.println("Erro - Klene");
						return false;
					}
				} else {
					System.out.println("Erro - Klene");
					return false;
				}
			}
			
			else if(entrada.charAt(i) == '+') {
				if(this.validPreview(i)) {
					if(this.isLetterLower(entrada.charAt(i-1)) || (entrada.charAt(i-1) == ')')) {
						System.out.println("Show - Klene positivo!");
					}else {
						System.out.println("Erro - Klene positivo");
						return false;
					}
				} else {
					System.out.println("Erro - Klene positivo");
					return false;
				}
			}
			
			else {
				System.out.println("Erro - Caractere Inv�lido");
				return false;
			}
		}
		
		if(parenthesisStack == 0) {
			System.out.println("Leitura de paretenses v�lida.");
		}else {
			System.out.println("Erro nos parenteses");
			return false;
		}
		
		return true;
	}
}
