package com.epam.rd.autocode.concurrenttictactoe;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerImpl implements Player {

	private static TicTacToe ticTacToe;
	private char mark;
	private PlayerStrategy strategy;
	private AtomicBoolean keepPlaying;

	public PlayerImpl(TicTacToe ticTacToe, char mark, PlayerStrategy strategy) {
		super();
		this.ticTacToe = ticTacToe;
		this.mark = mark;
		this.strategy = strategy;
		keepPlaying = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		// chequear si es mi turno o no, permanentemente
		// Valida si debe empezar o no (siempre empieza 'x')

		// valida si el juego sigue activo o ya se acabó (ya ganó el rival)
		// función para chequear el estado del juego
		// Ya ganó alguno
		// Hay empate
		// No ha ganado nadie --> Puedo jugar? la anterior marca es la mía o no? -->
		// jugar
		// Uso la estrategia para conocer la siguiente que voy a hacer
		// Con ese movimiento obtenido, valido si gano no para terminar el juego o
		// continuar
		// Pponer marca en tablero para que el otro hilo se ejecute
		synchronized (this) {
			while (keepPlaying.get()) {

				if (ticTacToe.lastMark() != mark) {
					if (!isADraw() && !hasSomeoneWon()) {
						Move nextMove = strategy.computeMove(mark, ticTacToe);
						if (willIWinint(nextMove.row, nextMove.column)) {
							keepPlaying.set(false);
						}
						ticTacToe.setMark(nextMove.row, nextMove.column, mark);
					} else {
						keepPlaying.set(false);
					}
				}
			}
		}
	}

	private synchronized boolean hasSomeoneWon() {

		for (int i = 0; i < 3; i++) {
			if (ticTacToe.table()[i][0] != ' ' && ticTacToe.table()[i][0] == ticTacToe.table()[i][1]
					&& ticTacToe.table()[i][1] == ticTacToe.table()[i][2]) {
				return true; // Ganador en fila i
			}
			if (ticTacToe.table()[0][i] != ' ' && ticTacToe.table()[0][i] == ticTacToe.table()[1][i]
					&& ticTacToe.table()[1][i] == ticTacToe.table()[2][i]) {
				return true; // Ganador en columna i
			}
		}

		// Verificar diagonales
		if (ticTacToe.table()[0][0] != ' ' && ticTacToe.table()[0][0] == ticTacToe.table()[1][1]
				&& ticTacToe.table()[1][1] == ticTacToe.table()[2][2]) {
			return true; // Ganador en diagonal principal
		}
		if (ticTacToe.table()[0][2] != ' ' && ticTacToe.table()[0][2] == ticTacToe.table()[1][1]
				&& ticTacToe.table()[1][1] == ticTacToe.table()[2][0]) {
			return true; // Ganador en diagonal secundaria
		}

		return false;
	}

	private synchronized boolean isADraw() {
		for (char[] row : ticTacToe.table()) {
			for (char cell : row) {
				if (cell == ' ') {
					return false; // Aún hay casillas vacías, el tablero no está lleno
				}
			}
		}
		return true; // Todas las casillas están ocupadas
	}

	private synchronized boolean willIWinint(int x, int y) {

		// Crear una copia temporal del tablero
		char[][] tempBoard = new char[ticTacToe.table().length][ticTacToe.table()[0].length];
		for (int i = 0; i < ticTacToe.table().length; i++) {
			tempBoard[i] = Arrays.copyOf(ticTacToe.table()[i], ticTacToe.table()[i].length);
		}

		// Realizar la jugada hipotética en la copia temporal
		tempBoard[x][y] = mark;

		// Verificar fila
		boolean rowWin = true;
		for (int col = 0; col < tempBoard.length; col++) {
			if (tempBoard[x][col] != mark) {
				rowWin = false;
				break;
			}
		}

		// Verificar columna
		boolean colWin = true;
		for (int row = 0; row < tempBoard.length; row++) {
			if (tempBoard[row][y] != mark) {
				colWin = false;
				break;
			}
		}

		// Verificar diagonal principal
		boolean mainDiagonalWin = true;
		for (int i = 0; i < tempBoard.length; i++) {
			if (tempBoard[i][i] != mark) {
				mainDiagonalWin = false;
				break;
			}
		}

		// Verificar diagonal secundaria
		boolean secondaryDiagonalWin = true;
		for (int i = 0; i < tempBoard.length; i++) {
			if (tempBoard[i][tempBoard.length - 1 - i] != mark) {
				secondaryDiagonalWin = false;
				break;
			}
		}

		return rowWin || colWin || mainDiagonalWin || secondaryDiagonalWin;
	}
}
