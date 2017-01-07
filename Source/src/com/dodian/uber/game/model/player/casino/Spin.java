package com.dodian.uber.game.model.player.casino;

import com.dodian.uber.game.Server;

public class Spin {
  Symbol[] symbols = new Symbol[3];
  int winnings = 0;

  public Spin(Symbol[] symbols) {
    this.symbols[0] = symbols[0];
    this.symbols[1] = symbols[1];
    this.symbols[2] = symbols[2];
    symbols[0].setColor("red");
    symbols[1].setColor("red");
    symbols[2].setColor("red");
    if (symbols[0].getId() == symbols[1].getId() && symbols[1].getId() == symbols[2].getId()) {
      switch (symbols[0].getId()) {
      case 6:
        winnings = Server.slots.slotsJackpot > 240000 ? Server.slots.slotsJackpot : 240000;
        break;
      case 1:
        winnings = 30000;
        break;
      case 2:
        winnings = 19000;
        break;
      case 3:
        // winnings = 14000;
        winnings = Server.slots.slotsJackpot > 240000 ? Server.slots.slotsJackpot : 240000;
        break;
      case 4:
        winnings = 11000;
        break;
      case 5:
        winnings = 9000;
        break;
      default:
        winnings = 0;
        break;
      }
      if (winnings > 0) {
        symbols[0].setColor("gre");
        symbols[1].setColor("gre");
        symbols[2].setColor("gre");
      }
    } else if (symbols[0].getId() == symbols[1].getId() || symbols[0].getId() == symbols[2].getId()
        || symbols[2].getId() == symbols[1].getId()) {
      int id = 0;
      if (symbols[0] == symbols[1]) {
        id = symbols[0].getId();
      } else if (symbols[0] == symbols[2]) {
        id = symbols[0].getId();
      } else if (symbols[1] == symbols[2]) {
        id = symbols[1].getId();
      }
      switch (id) {
      case 6:
        winnings = 5000;
        break;
      case 1:
        winnings = 2500;
        break;
      case 2:
        winnings = 1500;
        break;
      default:
        winnings = 0;
        break;
      }
      if (winnings > 0) {
        if (symbols[0].getId() == symbols[1].getId()) {
          symbols[0].setColor("gre");
          symbols[1].setColor("gre");
        } else if (symbols[0].getId() == symbols[2].getId()) {
          symbols[0].setColor("gre");
          symbols[2].setColor("gre");
        } else if (symbols[1].getId() == symbols[2].getId()) {
          symbols[1].setColor("gre");
          symbols[2].setColor("gre");
        }
      }
    }
  }

  public Symbol[] getSymbols() {
    return symbols;
  }

  public int getWinnings() {
    return winnings;
  }
}
