% JComboBox组件添加KeyListner
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

徐敬琪提出的这个问题，呵呵，以前没有注意到，就是为JComboBox组件添加KeyListner不起作用，我按照他说得添加了几乎所有JComboBox所支持的事件监听器都不好用，google了一下才知道，实际上，对于JComboBox设置为可编辑之后，要对他的KeyStroke进行监听，实际上是对他的editor的EditorComponent组件的KeyStroke事件进行监听（还记不记得JComboBox是由几个其他的组件组合而成的那？！事件上，这种情况下是要对JTextField组件上的KeyStroke进行监听），所以，使用类似的代码就可以正确监听到JComboBox上的KeyStroke事件了：


~~~~~~~ {.java}
comboPattern.getEditor().getEditorComponent().addKeyListener(new KeyListener(){
                            public void keyPressed(KeyEvent e)
                            {
                                //System.out.println("keyPressed";
                                JTextField jtf = (JTextField)e.getComponent();
                                if(!(jtf.getText()==null||"".equals(jtf.getText())))
                                {
                                    btnFind.setEnabled(true);
                                    btnReplace.setEnabled(true);
                                    btnReplaceAll.setEnabled(true);
                                }
                            }
                            public void keyTyped(KeyEvent e)
                            {
                                //System.out.println("keyTyped";
                            }
                            public void keyReleased(KeyEvent e)
                            {
                                //System.out.println("keyReleased";
                                JTextField jtf = (JTextField)e.getComponent();
                                if(jtf.getText()==null||"".equals(jtf.getText()))
                                {
                                    btnFind.setEnabled(false);
                                    btnReplace.setEnabled(false);
                                    btnReplaceAll.setEnabled(false);
                                }
                            }
                        });;
~~~~~~~

Thanks to Java Comunity Forum！