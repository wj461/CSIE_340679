/* HW1. Battle
 * This file contains two classes :
 * 		- Deck represents a pack of cards,
 * 		- Battle represents a battle game.
 */

import java.util.LinkedList;

class Deck { // represents a pack of cards

	LinkedList<Integer> cards;
	// The methods toString, hashCode, equals, and copy are used for
	// display and testing, you should not modify them.

	@Override
	public String toString() {
		return cards.toString();
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		Deck d = (Deck) o;
		return cards.equals(d.cards);
	}

	Deck copy() {
		Deck d = new Deck();
		for (Integer card : this.cards)
			d.cards.addLast(card);
		return d;
	}

	// constructor of an empty deck
	Deck() {
		cards = new LinkedList<Integer>();
	}

	// constructor from field
	Deck(LinkedList<Integer> cards) {
		this.cards = cards;
	}

	// constructor of a complete sorted deck of cards with nbVals values
	Deck(int nbVals) {
		cards = new LinkedList<Integer>();
		for (int j = 1; j <= nbVals; j++)
			for (int i = 0; i < 4; i++)
				cards.add(j);
	}

	// Question 1

	// takes a card from deck d to put it at the end of the current packet
	int pick(Deck d) {
		// throw new Error("Method pick(Deck d) to complete (Question 1)");
		if (!d.cards.isEmpty()) {
			int x = d.cards.removeFirst();
			cards.addLast(x);
			return x;
		} else {
			return -1;
		}
	}

	// takes all the cards from deck d to put them at the end of the current deck
	void pickAll(Deck d) {
		// throw new Error("Method pickAll(Deck d) to complete (Question 1)");
		while (!d.cards.isEmpty()) {
			pick(d);
		}
	}

	// checks if the current packet is valid
	boolean isValid(int nbVals) {
		// throw new Error("Method isValid(int nbVals) to complete (Question 1)");
		int[] numbers = new int[nbVals];
		for (Integer x : cards) {
			if (x < 1 || x > nbVals || numbers[x - 1] > 3)
				return false;
			numbers[x - 1]++;
		}
		return true;
	}

	// Question 2.1

	// chooses a position for the cut
	int cut() {
		// get the cards size
		int size = cards.size();
		int head = 0;
		for (int i = 0; i < size; i++) {
			if (Math.random() < 0.5) {
				head += 1;
			}
		}
		return head;
	}

	// cuts the current packet in two at the position given by cut()
	Deck split() {
		int c = cut();
		Deck newDeck = new Deck();
		for (int i = 0; i < c; i++) {
			newDeck.cards.addLast(cards.removeFirst());
		}
		return newDeck;
	}

	// Question 2.2

	// mixes the current deck and the deck d
	void riffleWith(Deck d) {
		Deck newDeck = new Deck();
		int a = d.cards.size();
		int b = cards.size();
		float p = (float) a / (a + b);
		while (!d.cards.isEmpty() && !cards.isEmpty()) {
			if (Math.random() < p) {
				newDeck.cards.addLast(d.cards.removeFirst());
			} else {
				newDeck.cards.addLast(cards.removeFirst());
			}
		}
		if (!d.cards.isEmpty()) {
			newDeck.pickAll(d);
		} else {
			newDeck.pickAll(this);
		}
		this.pickAll(newDeck);
	}

	// Question 2.3

	// shuffles the current deck using the riffle shuffle
	void riffleShuffle(int m) {
		for (int i = 0; i < m; i++) {
			Deck d = split();
			riffleWith(d);
		}
	}
}

class Battle { // represents a battle game

	Deck player1;
	Deck player2;
	Deck trick;

	// constructor of a battle without cards
	Battle() {
		player1 = new Deck();
		player2 = new Deck();
		trick = new Deck();
	}

	// constructor from fields
	Battle(Deck player1, Deck player2, Deck trick) {
		this.player1 = player1;
		this.player2 = player2;
		this.trick = trick;
	}

	// copy the battle
	Battle copy() {
		Battle r = new Battle();
		r.player1 = this.player1.copy();
		r.player2 = this.player2.copy();
		r.trick = this.trick.copy();
		return r;
	}

	// string representing the battle
	@Override
	public String toString() {
		return "Player 1 : " + player1.toString() + "\n" + "Player 2 : " + player2.toString() + "\nPli "
				+ trick.toString();
	}

	// Question 3.1

	// constructor of a battle with a deck of cards of nbVals values
	Battle(int nbVals) {
		Deck newDeck = new Deck(nbVals);
		player1 = new Deck();
		player2 = new Deck();
		trick = new Deck();
		newDeck.riffleShuffle(7);
		while (!newDeck.cards.isEmpty()) {
			player1.cards.addLast(newDeck.cards.removeFirst());
			if (newDeck.cards.isEmpty()) {
				break;
			}
			player2.cards.addLast(newDeck.cards.removeFirst());
		}
	}

	// Question 3.2

	// test if the game is over
	boolean isOver() {
		if (player1.cards.isEmpty() || player2.cards.isEmpty()) {
			return true;
		}
		return false;
	}

	// effectue un tour de jeu
	boolean oneRound() {
		if (player1.cards.isEmpty() || player2.cards.isEmpty()) {
			return false;
		}

		int player1Card = player1.cards.removeFirst();
		int player2Card = player2.cards.removeFirst();
		trick.cards.addLast(player1Card);
		trick.cards.addLast(player2Card);
		if (player1Card > player2Card) {
			player1.pickAll(trick);
			return true;
		} else if (player1Card < player2Card) {
			player2.pickAll(trick);
			return true;
		}
		// Battle
		while (player1Card == player2Card) {
			if (player1.cards.isEmpty() || player2.cards.isEmpty()) {
				return false;
			}
			trick.cards.addLast(player1.cards.removeFirst());
			trick.cards.addLast(player2.cards.removeFirst());
			if (player1.cards.isEmpty() || player2.cards.isEmpty()) {
				return false;
			}

			player1Card = player1.cards.removeFirst();
			player2Card = player2.cards.removeFirst();
			trick.cards.addLast(player1Card);
			trick.cards.addLast(player2Card);

			if (player1Card > player2Card) {
				player1.pickAll(trick);
				return true;
			} else if (player1Card < player2Card) {
				player2.pickAll(trick);
				return true;
			}
		}
		return false;
	}

	// Question 3.3

	// returns the winner
	int winner() {
		if (player1.cards.size() > player2.cards.size()) {
			return 1;
		} else if (player1.cards.size() < player2.cards.size()) {
			return 2;
		} else {
			return 0;
		}
	}

	// plays a game with a fixed maximum number of moves
	int game(int turns) {
		for (int i = 0; i < turns; i++) {
			if (!oneRound()) {
				return winner();
			}
		}
		return winner();
	}

	// Question 4.1

	// plays a game without limit of moves, but with detection of infinite games
	int game() {
		Battle b = copy();
		while (true) {
			if (!oneRound()) {
				return winner();
			}
			b.oneRound();
			if (!b.oneRound()) {
				continue;
			}

			if (b.toString().equals(this.toString())) {
				return 3;
			}
		}
	}

	// Question 4.2

	// performs statistics on the number of infinite games
	static void stats(int nbVals, int nbGames) {
		int player1Win = 0;
		int player2Win = 0;
		int draw = 0;
		int infinite = 0;
		for (int i = 0; i < nbGames; i++) {
			Battle b = new Battle(nbVals);
			if (b.game() == 0) {
				draw++;
			} else if (b.game() == 1) {
				player1Win++;
			} else if (b.game() == 2) {
				player2Win++;
			} else if (b.game() == 3) {
				infinite++;
			}
		}
		System.out.println("Player 1 wins : " + player1Win + " games");
		System.out.println("Player 2 wins : " + player2Win + " games");
		System.out.println("Draw : " + draw + " games");
		System.out.println("Infinite games : " + infinite + " games");
	}
}
