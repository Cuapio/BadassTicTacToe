/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.tictactoe;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.View;

/**
 *
 * @author Mario
 */
public class TicTacToeGui extends javax.swing.JFrame {
    private final HashSet<JButton> usersSelectedSquares = new HashSet<>();//set de cuadrados del usuario
    private final HashSet<JButton> machineSelectedSquares = new HashSet<>();//set de cuadrados de la maqina
    private final ArrayList<JButton> squares = new ArrayList<>();// ArrayList de los 9 cuadrados
    Iterator<JButton> squaresIterator;//iterador de todos los botones
    private final ArrayList<JButton> cornerSquares = new ArrayList<>();//cuadrados 1, 3, 7 y 9
    private final ArrayList<JButton> sideSquares;//cuadrados 2, 4, 6, 8
    private final HashSet<ArrayList<JButton>> winningCombinations = new HashSet<>();//combinaciones ganadoras(8)
    Iterator<ArrayList<JButton>> winningCombinationsIterator; //iterador de jugadas ganadoras

    private static int losingRecordsCounter=0;//contador de jugadas perdedoras aprendidas(se inicia cada sesión)
    private static final TicTacToeRecord losingRecords[][] = new TicTacToeRecord[50][2];
    private static final TicTacToeRecord[] currentGameLog = new TicTacToeRecord[9]; // Array para la bitácora (se reinicia cada juego)
    
    private final DefaultListModel<String> gameLogModel;//para alimentar el JList del registro de la partida 
    JList gameLogList;
    JScrollBar gameLogScrollBar;
    ChangeListener stateChanged;
    JScrollPane scrollableGameLogListView;
    Component[] scoreComponents;
    
    private byte userWinsCounter = 0;//victorias del usuario
    private byte machineWinsCounter = 0;//vitorias de la maquina
    private byte drawsCounter = 0; //Empates
    private byte roundsCounter = 0;
    private byte usedSquaresCounter = 0;
    
    private JButton machineChoice;
    private char starts = ' ';//Indica quien inicia
    private char userSign;
    private char machineSign;
    String winner;
    String level;
    String mode;
    
    /**
     * Creates new form TicTacToeGui
     */
    public TicTacToeGui() {
        initComponents();
        //Hashset of all buttons
        squares.add(btnTopLeftCorner);
        squares.add(btnTopSide);
        squares.add(btnTopRightCorner);
        squares.add(btnLeftSide);
        squares.add(btnCenter);
        squares.add(btnRightSide);
        squares.add(btnBottomLeftCorner);
        squares.add(btnBottomSide);
        squares.add(btnBottomRightCorner);
        
        //corner squares array
        cornerSquares.add(btnTopLeftCorner);
        cornerSquares.add(btnTopRightCorner);
        cornerSquares.add(btnBottomLeftCorner);
        cornerSquares.add(btnBottomRightCorner);
        
        this.sideSquares = new ArrayList<>();
        //side squares array
        sideSquares.add(btnTopSide);
        sideSquares.add(btnLeftSide);
        sideSquares.add(btnRightSide);
        sideSquares.add(btnBottomSide);
        
        // HashSet of Winning Combinations 
        winningCombinations.add(new ArrayList<JButton>() {{ add(btnTopLeftCorner); add(btnTopSide); add(btnTopRightCorner); }});//horizontal top Combination
        winningCombinations.add(new ArrayList<JButton>() {{ add(btnLeftSide); add(btnCenter); add(btnRightSide); }});//horizontal center Combination
        winningCombinations.add(new ArrayList<JButton>() {{ add(btnBottomLeftCorner); add(btnBottomSide); add(btnBottomRightCorner); }});//horizontal bottom Combination
        winningCombinations.add(new ArrayList<JButton>() {{ add(btnTopLeftCorner); add(btnLeftSide); add(btnBottomLeftCorner); }});//vertical left Combination
        winningCombinations.add(new ArrayList<JButton>() {{ add(btnTopSide); add(btnCenter); add(btnBottomSide); }});//vertical cneter Combination
        winningCombinations.add(new ArrayList<JButton>() {{ add(btnTopRightCorner); add(btnRightSide); add(btnBottomRightCorner); }});//vertical right Combination
        winningCombinations.add(new ArrayList<JButton>() {{ add(btnTopLeftCorner); add(btnCenter); add(btnBottomRightCorner); }});//diagonal top left to bottom right Combination
        winningCombinations.add(new ArrayList<JButton>() {{ add(btnTopRightCorner); add(btnCenter); add(btnBottomLeftCorner); }});//diagonal top right to bottom left Combination
                
        jToggleButton1.setSelected(false);
        jToggleButton1.setText("Yes");
        
        // Configuramos la vista del registro de la partida
        scoreComponents = jPanelScore.getComponents();
        gameLogModel = new DefaultListModel<>(); 
        gameLogList = new JList(gameLogModel);
        gameLogList.setSize(200, 96);
        scrollableGameLogListView = new JScrollPane();
        scrollableGameLogListView.setViewportView(gameLogList);
        scrollableGameLogListView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollableGameLogListView.setBounds(jPanelScore.getX() + 25, jPanelScore.getY(), 150, 99); 
        scrollableGameLogListView.setVisible(false);
        gameLogScrollBar = scrollableGameLogListView.getVerticalScrollBar();
        stateChanged = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // here the control if vertical scroll bar has reached the maximum value
                int maximum = gameLogScrollBar.getModel().getMaximum();
                gameLogScrollBar.setValue(maximum);
            }
        };
        gameLogScrollBar.addAdjustmentListener(new AdjustmentListener(){ 
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if(e.getValueIsAdjusting()) {
                    gameLogScrollBar.getModel().removeChangeListener(stateChanged);
                }
            }
        });
        this.add(scrollableGameLogListView);
        
        winner = verifyWinner();
        machineSign = 'O';
        userSign = 'X';
        starts = 'U';
        mode = "game";  // or review [PENDING] 
        //jComboBoxLevel.setSelectedIndex(0);//indica nivel inical
        level = (String) jComboBoxLevel.getSelectedItem(); 
        
        cleanTicTacToe();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        btnTopLeftCorner = new javax.swing.JButton();
        btnTopSide = new javax.swing.JButton();
        btnTopRightCorner = new javax.swing.JButton();
        btnLeftSide = new javax.swing.JButton();
        btnCenter = new javax.swing.JButton();
        btnRightSide = new javax.swing.JButton();
        btnBottomLeftCorner = new javax.swing.JButton();
        btnBottomSide = new javax.swing.JButton();
        btnBottomRightCorner = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxLevel = new javax.swing.JComboBox<>();
        jLabelLevel = new javax.swing.JLabel();
        jLabelStart = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanelScore = new javax.swing.JPanel();
        jLabelUserVictoriesCounter = new javax.swing.JLabel();
        jLabelDrawsCounter = new javax.swing.JLabel();
        jLabelUserVictories = new javax.swing.JLabel();
        jLabelDraws = new javax.swing.JLabel();
        jLabelMachineVictories = new javax.swing.JLabel();
        jLabelVictories = new javax.swing.JLabel();
        jLabelMachineVictoriesCounter = new javax.swing.JLabel();
        btnToggleViewRecord = new javax.swing.JButton();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnTopLeftCorner.setPreferredSize(new java.awt.Dimension(75, 75));
        btnTopLeftCorner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTopLeftCornerActionPerformed(evt);
            }
        });

        btnTopSide.setPreferredSize(new java.awt.Dimension(75, 75));
        btnTopSide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTopSideActionPerformed(evt);
            }
        });

        btnTopRightCorner.setPreferredSize(new java.awt.Dimension(75, 75));
        btnTopRightCorner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTopRightCornerActionPerformed(evt);
            }
        });

        btnLeftSide.setPreferredSize(new java.awt.Dimension(75, 75));
        btnLeftSide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeftSideActionPerformed(evt);
            }
        });

        btnCenter.setPreferredSize(new java.awt.Dimension(75, 75));
        btnCenter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCenterActionPerformed(evt);
            }
        });

        btnRightSide.setName(""); // NOI18N
        btnRightSide.setPreferredSize(new java.awt.Dimension(75, 75));
        btnRightSide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRightSideActionPerformed(evt);
            }
        });

        btnBottomLeftCorner.setPreferredSize(new java.awt.Dimension(75, 75));
        btnBottomLeftCorner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBottomLeftCornerActionPerformed(evt);
            }
        });

        btnBottomSide.setPreferredSize(new java.awt.Dimension(75, 75));
        btnBottomSide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBottomSideActionPerformed(evt);
            }
        });

        btnBottomRightCorner.setPreferredSize(new java.awt.Dimension(75, 75));
        btnBottomRightCorner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBottomRightCornerActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Rockwell", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("TIC TAC TOE");

        jComboBoxLevel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Human", "Demon Hunter", "Engineer must die", "Heaven and Hell", "Double Hell" }));
        jComboBoxLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxLevelActionPerformed(evt);
            }
        });

        jLabelLevel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabelLevel.setText("Level");

        jLabelStart.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabelStart.setText("User starts?");

        jToggleButton1.setText("YES");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jLabelUserVictoriesCounter.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabelUserVictoriesCounter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelUserVictoriesCounter.setText("0");

        jLabelDrawsCounter.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabelDrawsCounter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDrawsCounter.setText("0");

        jLabelUserVictories.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabelUserVictories.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelUserVictories.setText("User");

        jLabelDraws.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabelDraws.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDraws.setText("Draws");

        jLabelMachineVictories.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabelMachineVictories.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMachineVictories.setText("Machine");

        jLabelVictories.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabelVictories.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelVictories.setText("Victories");

        jLabelMachineVictoriesCounter.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabelMachineVictoriesCounter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMachineVictoriesCounter.setText("0");

        javax.swing.GroupLayout jPanelScoreLayout = new javax.swing.GroupLayout(jPanelScore);
        jPanelScore.setLayout(jPanelScoreLayout);
        jPanelScoreLayout.setHorizontalGroup(
            jPanelScoreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelScoreLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelScoreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelVictories, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelScoreLayout.createSequentialGroup()
                        .addGroup(jPanelScoreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelUserVictoriesCounter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelUserVictories, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelScoreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelMachineVictories, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelMachineVictoriesCounter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelScoreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelDrawsCounter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelDraws))))
                .addContainerGap())
        );
        jPanelScoreLayout.setVerticalGroup(
            jPanelScoreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelScoreLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelVictories)
                .addGap(4, 4, 4)
                .addGroup(jPanelScoreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMachineVictories)
                    .addComponent(jLabelUserVictories)
                    .addComponent(jLabelDraws))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelScoreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelUserVictoriesCounter)
                    .addComponent(jLabelMachineVictoriesCounter)
                    .addComponent(jLabelDrawsCounter)))
        );

        btnToggleViewRecord.setText("View Rounds");
        btnToggleViewRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggleViewRecordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnLeftSide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCenter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRightSide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBottomLeftCorner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBottomSide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBottomRightCorner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnTopLeftCorner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTopSide, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTopRightCorner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanelScore, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabelStart)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelLevel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxLevel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnToggleViewRecord)
                        .addGap(65, 65, 65))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnTopLeftCorner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTopSide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTopRightCorner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRightSide, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnLeftSide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCenter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBottomRightCorner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnBottomLeftCorner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnBottomSide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelLevel)
                            .addComponent(jComboBoxLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jToggleButton1)
                            .addComponent(jLabelStart))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelScore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnToggleViewRecord)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTopLeftCornerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopLeftCornerActionPerformed
        // TODO add your handling code here:
        pushSquare((JButton)evt.getSource(), "user");
    }//GEN-LAST:event_btnTopLeftCornerActionPerformed

    private void btnTopSideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopSideActionPerformed
        // TODO add your handling code here:
        pushSquare((JButton)evt.getSource(), "user");
    }//GEN-LAST:event_btnTopSideActionPerformed

    private void btnTopRightCornerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopRightCornerActionPerformed
        // TODO add your handling code here:
        pushSquare((JButton)evt.getSource(), "user"); 
    }//GEN-LAST:event_btnTopRightCornerActionPerformed

    private void btnLeftSideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeftSideActionPerformed
        // TODO add your handling code here:
        pushSquare((JButton)evt.getSource(), "user"); 
    }//GEN-LAST:event_btnLeftSideActionPerformed

    private void btnCenterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCenterActionPerformed
        // TODO add your handling code here:
        pushSquare((JButton)evt.getSource(), "user");  
    }//GEN-LAST:event_btnCenterActionPerformed

    private void btnRightSideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRightSideActionPerformed
        // TODO add your handling code here:
        pushSquare((JButton)evt.getSource(), "user");  
    }//GEN-LAST:event_btnRightSideActionPerformed

    private void btnBottomLeftCornerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBottomLeftCornerActionPerformed
        // TODO add your handling code here:
         pushSquare((JButton)evt.getSource(), "user");
    }//GEN-LAST:event_btnBottomLeftCornerActionPerformed

    private void btnBottomSideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBottomSideActionPerformed
        // TODO add your handling code here:
         pushSquare((JButton)evt.getSource(), "user");
    }//GEN-LAST:event_btnBottomSideActionPerformed

    private void btnBottomRightCornerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBottomRightCornerActionPerformed
        // TODO add your handling code here:
        pushSquare((JButton)evt.getSource(), "user");
    }//GEN-LAST:event_btnBottomRightCornerActionPerformed

    private void jComboBoxLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxLevelActionPerformed
        // TODO add your handling code here:
        if(usedSquaresCounter > 1 && "Double Hell".equalsIgnoreCase(level)) {
            JOptionPane.showMessageDialog(this, "Seas Mamooon!");
        }
        level = (String) jComboBoxLevel.getSelectedItem();//actualiza nivel
        gameLogModel.addElement("Round " + (roundsCounter + 1) + ": level " + (jComboBoxLevel.getSelectedIndex() + 1));
        gameLogScrollBar.getModel().addChangeListener(stateChanged);
    }//GEN-LAST:event_jComboBoxLevelActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        if (jToggleButton1.isSelected()) {
            jToggleButton1.setText("No"); 
            starts = 'M';
        } else {
            jToggleButton1.setText("Yes");  
            starts = 'U';
        }  
        cleanTicTacToe();  
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void btnToggleViewRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleViewRecordActionPerformed
        // TODO add your handling code here:
        if("View Rounds".equalsIgnoreCase(btnToggleViewRecord.getText())){ 
            btnToggleViewRecord.setText("Hide log");
            jPanelScore.setVisible(false);
            for(Component component : scoreComponents) {
                component.setVisible(false);
            }
            scrollableGameLogListView.setVisible(true);  
            scrollableGameLogListView.getVerticalScrollBar().setValue(scrollableGameLogListView.getVerticalScrollBar().getMaximum());
            gameLogScrollBar.getModel().addChangeListener(stateChanged);
        } else {
            btnToggleViewRecord.setText("View Rounds");
            scrollableGameLogListView.setVisible(false);
            jPanelScore.setVisible(true);
            for(Component component : scoreComponents) {
                component.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnToggleViewRecordActionPerformed

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TicTacToeGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TicTacToeGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TicTacToeGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TicTacToeGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TicTacToeGui().setVisible(true);
            }
        });
    }
        
    private void cleanTicTacToe (){
        System.out.println("Last winner is " + winner);
        squaresIterator = squares.iterator();
        while(squaresIterator.hasNext()){
            JButton currentSquare = squaresIterator.next();
            currentSquare.setText("");
            currentSquare.setEnabled(true);
        }
        roundsCounter = (byte) (userWinsCounter + machineWinsCounter + drawsCounter);
        usedSquaresCounter = 0;
        usersSelectedSquares.clear();
        machineSelectedSquares.clear();
        for (TicTacToeRecord nthRecord : currentGameLog) {
            nthRecord = null;
        }
        jLabelUserVictoriesCounter.setText( "" + userWinsCounter );
        jLabelMachineVictoriesCounter.setText( "" + machineWinsCounter );
        jLabelDrawsCounter.setText( "" + drawsCounter );
        gameLogModel.addElement("Round " + (roundsCounter + 1) + ": level " + (jComboBoxLevel.getSelectedIndex() + 1));
        if("M".equalsIgnoreCase(starts + "")){
            machineChoice = machineReply();
            pushSquare(machineChoice, "machine");
        }
    }
    
    private void pushSquare(JButton square, String user){
        square.setEnabled(false);
        square.setFont(new Font("Tahoma", Font.BOLD, 40));
        if("user".equalsIgnoreCase(user)) {
            square.setText(""+userSign);
            square.setForeground(Color.BLACK);
            usersSelectedSquares.add(square);
            //Se graba antes de aumentar el contador de cuadrados para que no hay errores logicos
            recordChoice(new TicTacToeRecord("user   ", squares.indexOf(square), ' '));
            usedSquaresCounter++;
            winner = verifyWinner();
            if("user".equalsIgnoreCase(winner)){
                if("Double Hell".equalsIgnoreCase(level)) {
                    recordLearning();
                }
                JOptionPane.showMessageDialog(this, "You Winner! Respect.");
                userWinsCounter++;
                cleanTicTacToe();
            } else if("none".equalsIgnoreCase(winner)){
                JOptionPane.showMessageDialog(this, "Nothing For Anyone!");
                drawsCounter++;
                cleanTicTacToe();
            } else {
                machineChoice = machineReply();
                pushSquare(machineChoice, "machine");
            } 
        } else if("machine".equalsIgnoreCase(user)) {
            square.setText("" + machineSign);
            square.setForeground(Color.BLUE);
            machineSelectedSquares.add(square);
            usedSquaresCounter++;
            winner = verifyWinner();
            if("machine".equalsIgnoreCase(winner)) {
                JOptionPane.showMessageDialog(this, "You Loser! Maybe Next Time...");
                machineWinsCounter++;
                cleanTicTacToe();
            } else if("none".equalsIgnoreCase(winner)){
                JOptionPane.showMessageDialog(this, "Nothing For Anyone!");
                drawsCounter++;
                cleanTicTacToe();
            }
        }
    }
    
    // establece el ganador hasta el momento: "user" o "machine"; usa "none" para cuando el gato llega a un final
    // "wanted"(se busca) para cuando no hay ganador pero hay casillas vacías
    private String verifyWinner() {
        winner = "wanted";
        // empate si todas las casillas están llenas
        if(usedSquaresCounter == 9) winner = "none";
        // Determinar si ya ganó el usuario o la maquina enviando su conjunto de cuadrados elegidos
        if(isWinner(usersSelectedSquares)) 
            winner = "user";
        else if(isWinner(machineSelectedSquares)) 
            winner = "machine";

        return winner;
    }
    
    //devuelve si un jugador (usuario o maquina) ha ganado (boolean)
    private Boolean isWinner(HashSet<JButton> playerSquares) {
        ArrayList<JButton> currentWinningCombination;
        boolean hasAWinningCombination = false;
        
        winningCombinationsIterator = winningCombinations.iterator();
        while(winningCombinationsIterator.hasNext() && !hasAWinningCombination){
            currentWinningCombination = winningCombinationsIterator.next();
            hasAWinningCombination = playerSquares.containsAll(currentWinningCombination);
        }
        
        return hasAWinningCombination;
    }
    
    // identifica si existe una posición ganadora o a tapar en la combinación indicada
    private JButton verifyOneSquareLeft (ArrayList<JButton> winningCombination, HashSet<JButton> playersSquares) {
        int i, usedSquares=0, unusedSquares=0;
        JButton chosenSquare = null, tempSquare = null;
        
        for(i = 0; i<3; i++) {
            JButton nthButton = winningCombination.get(i);
            if(playersSquares.contains(nthButton)) usedSquares++;
            else if(nthButton.isEnabled()) {
                unusedSquares++; 
                tempSquare = nthButton;
            }
        }
        
        if(usedSquares == 2 && unusedSquares == 1) chosenSquare = tempSquare;
        return chosenSquare;
    }
    
    // verifica si existe alguna jugada a tapar o ganadoras.
    // Si existen ambas, se quedan las ganadoras
    // en caso de no existir ninguna de las dos, devuelve null
    private JButton smartChoice() {
        JButton tempSquare, chosenSquare = null;
        
        //buscar juagas a tapar del usuario
        winningCombinationsIterator = winningCombinations.iterator();
        while(winningCombinationsIterator.hasNext()){
            ArrayList<JButton> currentWinningCombination = winningCombinationsIterator.next();
            tempSquare = verifyOneSquareLeft(currentWinningCombination, usersSelectedSquares); 
            if (tempSquare != null) chosenSquare = tempSquare;
        }
        
        // buscar jugadas ganadoras de la maquina
        winningCombinationsIterator = winningCombinations.iterator();
        while(winningCombinationsIterator.hasNext()){
            ArrayList<JButton> currentWinningCombination = winningCombinationsIterator.next();
            tempSquare = verifyOneSquareLeft(currentWinningCombination, machineSelectedSquares); 
            if (tempSquare != null) chosenSquare = tempSquare;
        }

        return chosenSquare;
    }
    
    // Elige un cuadrado al azar
    private JButton randomChoice() {
        if(usedSquaresCounter == 9) return null;
        JButton chosenSquare;
        do {
            int rndNumber = (int) (Math.random() * 9);
            chosenSquare = squares.get(rndNumber);
        } while (!chosenSquare.isEnabled());
        return chosenSquare;
    }
    
    //Encuentra el primer cuadrado disponible
    private JButton firstAvailableChoice() {
//        if(usedSquaresCounter == 9) return null;
        JButton chosenSquare = null;
        squaresIterator = squares.iterator();
        JButton currentSquare;
        do {
            currentSquare = squaresIterator.next();
        }
        while(squaresIterator.hasNext() && !currentSquare.isEnabled());
        chosenSquare = currentSquare;
        return chosenSquare;
    }
    
    // examina si una jugada es perdedora, es decir, si al hacerla se está entregando la partida
    // a partir de estrategias prefijadas de inicio
    private boolean isLoser(JButton candidateSquare) {
        boolean isALoserChoice = false;

        // si la primera jugada la hizo el jugador y le toca a la computadora
        if (usersSelectedSquares.size() == 1 && machineSelectedSquares.isEmpty()) {
            // si el jugador tiró en una esquina es perdedora tirar en otro lugar que no sea el centro
            JButton firstElement = usersSelectedSquares.iterator().next();
            boolean isACorner = cornerSquares.contains(firstElement);
            if (isACorner) {
                if (candidateSquare != btnCenter) {
                    isALoserChoice=true;
                }
            }

            // si tiró en un cuadrado externo central(side Squares)(2, 4, 6 u 8) no debe tirarse en esquina opuesta
            boolean isASide = sideSquares.contains(firstElement);
            if(isASide) {
                if (firstElement == btnTopSide) {
                    if (candidateSquare == btnBottomLeftCorner || candidateSquare == btnBottomRightCorner)
                        isALoserChoice=true;
                }
                if (firstElement == btnLeftSide) {
                    if (candidateSquare == btnTopRightCorner || candidateSquare == btnBottomRightCorner)
                        isALoserChoice=true;
                }
                if (firstElement == btnRightSide) {
                    if (candidateSquare == btnTopLeftCorner || candidateSquare == btnBottomLeftCorner)
                        isALoserChoice=true;
                }
                if (firstElement == btnBottomSide) {
                    if (candidateSquare == btnTopLeftCorner || candidateSquare == btnTopRightCorner)
                        isALoserChoice=true;
                }
            }  
        }
        return isALoserChoice;
    }
    
    
    // examina si una jugada es perdedora a partir de su aprendizaje de errores,
    // es decir, si al hacerla se está entregando la partida
    private boolean isLoserSmart(JButton candidateSquare) {
        int i; 
        JButton userFisrtChoice = squares.get(0);
        boolean isALoserChoice=false;

        // cuenta las jugadas del jugador y la máquina
        
        // si la primera jugada la hizo el jugador y le toca a la computadora
        if ( usersSelectedSquares.size() == 1 && machineSelectedSquares.isEmpty() ) {
            System.out.println("Buscando si " + (squares.indexOf(userFisrtChoice) + 1) + " " + (squares.indexOf(candidateSquare) + 1) +  " es perdedora");
            for (i=1; i<=losingRecordsCounter; i++) {
                if ((losingRecords[i][0].squareIndex == squares.indexOf(userFisrtChoice) ) &&
                (losingRecords[i][1].squareIndex == squares.indexOf(candidateSquare) ) ) {
                    isALoserChoice = true;
                    System.out.print("\nLocalicé jugada perdedora " + (squares.indexOf(userFisrtChoice) + 1) +  " " + (squares.indexOf(candidateSquare) + 1) + "\n");
                }
            }
        }
        if (isALoserChoice) {
            System.out.print("\nHemos desechado la jugada " + (squares.indexOf(candidateSquare) + 1) + " con base en lo aprendido.\n ");
        }
        return isALoserChoice;
    }
    
    
    // devuelve el lugar donde va a tirar la máquina según el nivel de dificultad
    private JButton machineReply() {
        JButton chosenSquare = null;
        
        // el lugar en que se tirará es la primer casilla vacía encontrada
        if ("Human".equalsIgnoreCase(level)) {
            chosenSquare = firstAvailableChoice();
        }

        // se tira completamente al azar
        if ("Demon Hunter".equalsIgnoreCase(level)) {
            chosenSquare = randomChoice();
        }

        // verifica si hay jugadas ganadoras o a tapar. Sino al azar
        if ("Engineer must die".equalsIgnoreCase(level)) {
            chosenSquare = smartChoice();    // jugadas ganadoras o a tapar
            if (chosenSquare == null) chosenSquare = randomChoice();
        }

        // verifica si hay jugadas ganadoras o a tapar.
        // Después esquiva jugadas perdedoras fijas. Sino al azar.
        if ("Heaven And Hell".equalsIgnoreCase(level)) {
            chosenSquare = smartChoice();   // ubica jugadas ganadoras o a tapar
            if (chosenSquare == null) {
                do {                        // juega al azar eludiendo combinaciones perdedoras fijas
                    
                    chosenSquare = randomChoice();
                } while( (!chosenSquare.isEnabled()) || (isLoser(chosenSquare)) );
            }
        }

        // verifica si hay jugadas ganadoras o a tapar.
        // Después esquiva jugadas perdedoras POR SU APRENDIZAJE. Sino al azar.
        if ("Double Hell".equalsIgnoreCase(level)) {
            chosenSquare = smartChoice();   // ubica jugadas ganadoras o a tapar
            if (chosenSquare != null) {//Si fue forzada se manda 'F' de jugada forzada en el registro
                recordChoice(new TicTacToeRecord("machine", squares.indexOf(chosenSquare), 'F'));
            } else { //Si no fue forzada se manda 'L' de jugada libre en el registro
                do { // juega al azar eludiendo combinaciones perdedoras POR SU APRENDIZAJE
                    chosenSquare = randomChoice();
                } while( (!chosenSquare.isEnabled()) || (isLoserSmart(chosenSquare)) );
                recordChoice(new TicTacToeRecord("machine", squares.indexOf(chosenSquare), 'L'));
            }
        } else {
            recordChoice(new TicTacToeRecord("machine", squares.indexOf(chosenSquare), ' '));
        }

        return chosenSquare;
    }
    
    private void updateGameLogView(TicTacToeRecord record) {
        int squarePosition;
        String player;
        char type;
        
        player = record.player;
        squarePosition = record.squareIndex + 1;
        type = record.type;
        gameLogModel.addElement((usedSquaresCounter + 1) + ". " + player + "   " + squarePosition + "   " + type);
        gameLogScrollBar.getModel().addChangeListener(stateChanged);
    }
    
    // rutina para grabar cada jugada en la bitácora del juego
    private void recordChoice(TicTacToeRecord record) {
        if(usedSquaresCounter <= 8)
            currentGameLog[usedSquaresCounter] = record;
        updateGameLogView(record);
    } 
    
    private void printGameLog(){
        int i;
        int squarePosition;
        String player;
        TicTacToeRecord currentRecord;
        char type;
        System.out.println("\n===Bitacora de la partida===");
        System.out.println("   Player-Square-type ");
        for(i=0; i<usedSquaresCounter; i++) {
            currentRecord = currentGameLog[i];
            if(!(currentRecord instanceof TicTacToeRecord))break;
            player = currentRecord.player;
            squarePosition = currentRecord.squareIndex + 1;
            type = (currentRecord.type == ' ') ? 'J' : currentRecord.type;
            System.out.println( (i+1) + ". " + player + " - " + squarePosition + " - " + type);
        }
        System.out.println("==============================");
    }
    
    // graba la jugada perdedora con la que nos acaban de ganar, es decir,
    // la tirada con la que inició el usuario y la que la máquina hizo a continuación
    private void recordLearning() {
        int notForcedOnesCounter = 0, i, indexOfLosingRecord = 0;//numtirada = indexOfLoserRecord, cuenteLibres = notForcedOnesCounter
        TicTacToeRecord firstRecord;//tiradaprevia
        TicTacToeRecord losingRecord = null;//tirada
        boolean isANewLosingRecord=true;
    
        printGameLog();
        // rastrea en la bitácora del juego cuantas jugadas 'libres' se hicieron
        for (i=0; i<usedSquaresCounter; i++) {
            if (currentGameLog[i].type == 'L' ) {
                indexOfLosingRecord = i;
                losingRecord = currentGameLog[i];
                notForcedOnesCounter++;
            }
        }

        // si solo hubo una jugada 'libre' después de la primera del usuario
        // eso quiere decir que esa fue la que provocó perder la partida
        if (notForcedOnesCounter == 1 && indexOfLosingRecord == 1) {
            firstRecord = currentGameLog[0];
            System.out.print("\nPerdimos al tirar " + (losingRecord.squareIndex + 1) + " después de que el usuario inició con " + (firstRecord.squareIndex + 1) + " \n");
            System.out.print("Queda archivada esa opción para que no vuelva a suceder.\n");

            // graba esa jugada, previa verificación de que no está registrada
            for (i=1; i < losingRecordsCounter; i++) {
                if ((losingRecords[losingRecordsCounter][0] == firstRecord) &&
                (losingRecords[losingRecordsCounter][1]==losingRecord)) {
                    isANewLosingRecord = false;
                }
            }
            if (isANewLosingRecord) {
                losingRecordsCounter++;
                losingRecords[losingRecordsCounter][0]= firstRecord;
                losingRecords[losingRecordsCounter][1]=losingRecord;
            }
        }

        // mostrar aprendizaje hasta el momento
        System.out.print("--------------------------\n");
        System.out.print("Las jugadas 'perdedoras' registradas hasta el momento son: \n");
        for (i=1; i<=losingRecordsCounter; i++) {
            System.out.print("  { usuario=" + (losingRecords[i][0].squareIndex + 1) + "- máquina=" + (losingRecords[i][1].squareIndex + 1) + " }\n");
        }
        System.out.print("--------------------------\n");
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBottomLeftCorner;
    private javax.swing.JButton btnBottomRightCorner;
    private javax.swing.JButton btnBottomSide;
    private javax.swing.JButton btnCenter;
    private javax.swing.JButton btnLeftSide;
    private javax.swing.JButton btnRightSide;
    private javax.swing.JButton btnToggleViewRecord;
    private javax.swing.JButton btnTopLeftCorner;
    private javax.swing.JButton btnTopRightCorner;
    private javax.swing.JButton btnTopSide;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> jComboBoxLevel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelDraws;
    private javax.swing.JLabel jLabelDrawsCounter;
    private javax.swing.JLabel jLabelLevel;
    private javax.swing.JLabel jLabelMachineVictories;
    private javax.swing.JLabel jLabelMachineVictoriesCounter;
    private javax.swing.JLabel jLabelStart;
    private javax.swing.JLabel jLabelUserVictories;
    private javax.swing.JLabel jLabelUserVictoriesCounter;
    private javax.swing.JLabel jLabelVictories;
    private javax.swing.JPanel jPanelScore;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}
