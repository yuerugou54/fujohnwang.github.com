% RTFEditor的相关总结
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

<pre>
 这几天一直在为livedoor finance系统的一个有关客户信息编辑管理模块瞎忙活，呵呵，做做investigation，写写demo，不知不觉中，这个demo就跟一个编辑器差不多了，呵呵，好像比某些还没做完善的编辑器要够用的多，
    不过，现在的东西还有些许限制，但是大体上都可以了，字体，大小，对齐，打印，搜索，等等等等。
   罗列些许code如下，以记历程（没有refactor，所以，某些code可能看起来很丑陋，hoho）：
【1】Printer准备部分代码片断：
SmartJPrint.setLicenseKey("ATEU-QRZB-4FA5-41FD";
        printer = new AtDocumentPrinter();
        //init page header and footer
        printer.setPageHeaderFooterListener(new PageHeaderFooterListener(){
            int pageCount = 0;
            public void setPageHeaderAndFooter(PageHeaderFooterRenderer r)
            {
                AtHeaderFooterElement header = new AtHeaderFooterElement("",new Font("Times New Roman",Font.BOLD,20),Color.RED,Color.blue,true,1);

                AtHeaderFooterElement footer = new AtHeaderFooterElement("Page "+r.getPageNumber()+"/"+r.getTotalPageCount(),new Font("Times New Roman",Font.BOLD,20),Color.RED,Color.blue,true,1);
                if(r.getPageNumber()==1)
                {
                    header.setStr("Livedoor Editor 1.0 version";
                }
                r.setHeader(header);
                r.setFooter(footer);
            }
        });
        printer.setPreviewPaneSize(new Dimension(800,600));
【2】初始化系统字体部分，呵呵，没有做过之前，估计会在这个问题上困扰很久哦，总不能就硬编码每一种字体吧！
private void initFontAndSize()
    {
        // init fonts
        java.awt.GraphicsEnvironment gEnv = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = gEnv.getAvailableFontFamilyNames();
        for(int i=0;i<fonts.length;i++)
        {
            this.fontCombo.addItem(""+fonts[i]);
        }
        //----------init font size------------------
        for (int i = 6; i <= 100; i = i + 2)
            this.sizeCombo.addItem("" + i);

        sizeCombo.setSelectedItem("12";
        //sizeCombo.setEditable(true);
    }
【3】Redo和Undo部分的初始化部分代码（抄袭来的哦，）：
private void redoUndoInit()
    {
        editor.getStyledDocument().addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent evt) {
                manager.addEdit(evt.getEdit());
            }
        });

        // Create an undo action and add it to the text component
        editor.getActionMap().put("Undo",
                                    new AbstractAction("Undo" {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (manager.canUndo()) {
                        manager.undo();
                    }
                }
                catch (CannotUndoException e) {
                }
            }
        });

        // Bind the undo action to ctl-Z
        editor.getInputMap().put(KeyStroke.getKeyStroke("control Z", "Undo";

        // Create a redo action and add it to the text component
        editor.getActionMap().put("Redo",
                                    new AbstractAction("Redo" {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (manager.canRedo()) {
                        manager.redo();
                    }
                }
                catch (CannotRedoException e) {
                }
            }
        });

        // Bind the redo action to ctl-Y
        editor.getInputMap().put(KeyStroke.getKeyStroke("control Y", "Redo";
    }
另外，如果有redo和undo按钮，也需要为按钮添加action，比如，redo按钮需要
redoButton.setAction(new AbstractAction("Redo" {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (manager.canRedo()) {
                        manager.redo();
                    }
                }
                catch (CannotRedoException e) {
                }
            }
        }
        ;
【4】插入图片或者component等都很easy，因为JTextPane都提供了支持，这里就不多说了，查API ref就可以；
【5】Save，SaveAs和Open等存在一定的问题，因为Jdk提供的RTFEditorKit实际上实现的功能不全，虽然他能够正确保存文件为RTF格式，而且，其他RTF编辑器也可以读取他的文件，他同样可以读取其他RTF编辑器的RTF文件，但是问题是，使用他提供的read和write方法来Open和Save文件的时候，他不能对图片等进行正确的保存，所以，在我们的系统中，这是不允许的；最终我们选择了sun提供的对象流序列化方式，以对象流对文件进行保存和打开，虽然这种形式保存的文件不通用，（即使你保存文件为后缀的rtf，其他编辑器同样不能正确读取）但是，相对于我们的系统，这已经足够了，因为现在可以正确保存文件内容并打印。这部分的打开保存代码太长，放在这片blog的后续章节中附上。
【6】search部分的功能我这里的demo以较为简单的逻辑实现，更全面的实现在徐敬琪那里实现，实现估计也挺麻烦的，补贴了，also附录到下一篇。
【7】设置字体，字体大小，颜色等，原理相同，都是使用StyleConstants和XXAttibuteSet来处理，这里贴一下颜色的设置吧：
Color color = JColorChooser.showDialog(this,"",Color.BLUE);
        if(color==null)
            return;
        //editor.setBackground(color);
        int start = editor.getSelectionStart();
        int end = editor.getSelectionEnd();
        SimpleAttributeSet sset = new SimpleAttributeSet();
        StyleConstants.setForeground(sset,color);
        if(start!=end)
        {
            editor.getStyledDocument().setCharacterAttributes(start,end-start,sset,false);
        }
        else
        {
            StyleConstants.setForeground(kit.getInputAttributes(),color);
        }
【8】打印，有许多解决方案，jdk1.2提供的打印模型，jdk1.4新提供的Java Print Service，以及SWT提供的打印功能等，但是，因为时间较为紧张，没有过多时间去研读文档，所以，直接找了一个类库解决打印和打印预览问题。
void printButton_actionPerformed(ActionEvent e) {
          printer.print(editor,PageNoPainter.LOWER_CENTER);
    }
void browerButton_actionPerformed(ActionEvent e) {
        //do Printing Preview job here
        //printer.preview(editor,PageNoPainter.LOWER_RIGHT,false);
        printer.preview(editor,PageNoPainter.LOWER_CENTER,this);
        //-------------------------------------------------------------------
        //Custom the preview buttons on the preview Frame
        //-------------------------------------------------------------------
        //  You must invoke the printer's getPreviewWindow() method after its
        //  preview() method has been invoked, otherwise, the getPreviewWindow()
        //  method will return null. As a result ,a NULLPointerException will occour.
        //-------------------------------------------------------------------
        AtPreviewFrame previewWin = printer.getPreviewWindow();
        previewWin.setPreviewButtonVisible(AtPreviewFrame.PDF_BUTTON,false);
        previewWin.setPreviewButtonVisible(AtPreviewFrame.HELP_BUTTON,false);

    }
－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
ok，以上就先写这么些吧，待续Save，OPen等长代码篇。
</pre>