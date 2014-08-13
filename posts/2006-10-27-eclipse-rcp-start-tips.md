% Eclipse RCP start tips
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

The blog has not been updated for such a long long time, so today, let's say something...

写点儿有关RCP的小tips吧，反正也不知写些什么东西，无意间翻硬盘看到这个freemind文件，故此，罗列与此：

# Open View Tip

如果要在RCP中打开相应的View，你需要通过类似一下代码：

`window.getActivePage().showView(viewId, IWorkbenchPage.VIEW_ACTIVATE);`

不过，如果不在Perspective先行指定要打开的view所在位置的话，RCP应该不会知道你想让他帮你把该View放到什么位置，所以，我们还需要在Perspective中指定View的位置：

~~~~~~~ {.java}
layout.createPlaceholderFolder("panelFolder",IPageLayout.TOP,IPageLayout.NULL_RATIO,layout.getEditorArea()).addPlaceholder(AdministrationView.ID); 
(Perspective.java)
~~~~~~~

# How to Add Widget onto Table Cell


~~~~~~~ {.java}
Table table = new Table(top, SWT.NONE);
table.setHeaderVisible(true);
table.setLinesVisible(true);
table.setBounds(new org.eclipse.swt.graphics.Rectangle(47,67,190,70));
TableColumn tableColumn = new TableColumn(table, SWT.NONE);
tableColumn.setWidth(100);
tableColumn.setText("Check Column");

TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
tableColumn1.setWidth(100);
tableColumn1.setText("Combo Column");

TableItem tableItem=new TableItem(table,SWT.NONE);
TableEditor editor = new TableEditor (table);

Button checkButton = new Button(table, SWT.CHECK);
checkButton.pack();

editor.minimumWidth = checkButton.getSize ().x;
editor.horizontalAlignment = SWT.CENTER;
editor.setEditor(checkButton, tableItem, 0);
editor = new TableEditor (table);

Combo combo = new Combo(table, SWT.CHECK);
combo.pack();

editor.minimumWidth = combo.getSize ().x;
editor.horizontalAlignment = SWT.CENTER;
editor.setEditor(combo, tableItem, 1);
~~~~~~~

# How to get the Shell reference from ViewPart?!

`viewPart.getViewSite().getShell()`

# how to restart eclipse RCP application in program?!

`PlatformUI.getWorkbench().restart();`

# View和Editor的title圆润风格如何显示?

在RCP的product配置文件中(注意preferenceCustomization属性):

~~~~~~~ {.xml}
<extension
         id="product"
         point="org.eclipse.core.runtime.products">
      <product
            application="cn.bestwiz.jhf.dealer.demo.application"
            name="Dealer Demo">
         <property
               name="windowImages"
               value="/cn.bestwiz.jhf.dealer.demo/icons/letian.GIF"/>
         <property
               name="preferenceCustomization"
               value="plugin_customization.ini"/>
      </product>
   </extension>
~~~~~~~

其中，plugin_customization.ini文件的内容类似于（注意SHOW_TRADITIONAL_STYLE_TABS属性）：
<pre>
org.eclipse.ui/DOCK_PERSPECTIVE_BAR=topRight
org.eclipse.ui/SHOW_PROGRESS_ON_STARTUP=false
org.eclipse.ui/SHOW_TRADITIONAL_STYLE_TABS=false
org.eclipse.ui/defaultPerspectiveId=cn.bestwiz.jhf.dealerdesk.perspective
org.eclipse.update.core/org.eclipse.update.core.updateVersions=compatible
</pre>

OK,that's all, if something new comes , append it later.



