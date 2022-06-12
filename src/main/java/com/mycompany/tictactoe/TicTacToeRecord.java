/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoe;

import javax.swing.JButton;

/**
 *
 * @author Mario
 */
public class TicTacToeRecord {
    String player;
    int squareIndex;
    char type;

    public TicTacToeRecord(String player, int squareIndex, char type) {
        this.player = player;
        this.squareIndex = squareIndex;
        this.type = type;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getSquareIndex() {
        return squareIndex;
    }

    public void setSquareIndex(int squareIndex) {
        this.squareIndex = squareIndex;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }
    
    

    @Override
    public String toString() {
        return super.toString(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
    
    
}
