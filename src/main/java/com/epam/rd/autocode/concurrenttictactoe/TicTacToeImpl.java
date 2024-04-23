package com.epam.rd.autocode.concurrenttictactoe;

public class TicTacToeImpl implements TicTacToe {

	private static char[][] table;
	private static char lastMark;

	public TicTacToeImpl() {
		table = new char[3][3];

		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[0].length; j++) {
				table[i][j] = ' ';
			}
		}

		lastMark = ' ';
	}

	@Override
	public void setMark(int x, int y, char mark) {
		// ocupar posición en la matriz
		// debe ser sincrónica
		// cada jugador se puede ejecutar mas de una vez seguida, cada hilo
		// No hay manera de sincronizar el turno uno a uno
		synchronized (Thread.class) {
			if (' ' == table[x][y]) {
				table[x][y] = mark;
				lastMark = mark;
			} else {
				throw new IllegalArgumentException();
			}
		}
	}

	@Override
	public char[][] table() {
		synchronized (Thread.class) {
			return table;
		}
	}

	@Override
	public char lastMark() {
		// último en marcar
		// debe ser sincrónica
		synchronized (Thread.class) {
			return lastMark;
		}
	}

}
