% 我的Swing Jide WizardDialog实践个论 -- 外部化，统一化, 职责化你的代码

# 楔子 

感觉好久没写技术方面的博客了，沉寂了这么久，今天正好借机疏解一下自己的“郁闷”之情。自从8月中旬加入花旗软件（大连），转眼已经快三个月了，原先的美好愿望和希冀，现在却由淡淡的“忧伤”和“郁闷”而替代，原因或许很多，也或许根本就没有啥，纯粹庸人自扰：

1. 书稿写完了，提交给出版社之后的空虚？
2. 因为前女友家里人全员反对而分开后的郁郁寡欢？
3. 抑或是现在的工作与当初的设想大相径庭？！
   
道不清说不明的，反正这阵子一直是过得不快乐就是了。     其实，花旗大连这边的工作氛围是比较轻松的，应该说正是自己当时所希望的，但是，现实的情况却是，闲大法了也不好，当你已经习惯了飚车的速度感，猛然让你陷入闹市的堵车当中，应该是比较烦躁的吧？！ 

还记得自己当初刚进花旗软件（大连）时候的激情四射，狂啃项目组中业务相关的金融资料的情景，可是随着时间的流逝，才发现，短期内自己是多么的无助，呵呵，银行系统内部系统的庞大，又怎么可能一朝之间让你了然于心那？！现在的感觉真的只能用那句“心有余而力不足”来形容了。 

当前项目组的技术架构虽然很简单，但是，前面写了两年多的codebase却着实让我无能为力，不能说代码很烂，但是，各种细节之间的纠缠却非我这刚进来的newbie所能掌控的，所以，每每有新的issue交给我处理，都是小心翼翼的，能不动之前的codebase就不动， 
要知道，"失节"事小，"驾崩"事大，这可是金融系统，稍一不慎把系统搞挂掉，那可不是闹着玩的（虽然发布之前回旋余地大一些）。 

不过那，不去动之前的codebase并不是说这些codebase有多好，有多容易维护，实际上，整个项目有些“挂羊头，卖狗肉”的味道，说是敏捷开发，但没有看到多少敏捷的实践；说是使用Spring框架，却让它形同虚设；说是用Hibernate做数据访问，却仅仅用它来做为调用存储过程的中间层，只用了调用存储过程后能够帮助你将结果映射到结果对象这一点点功能，个人感觉就是Hibernate在这里真的是“生不逢时”… 

当然啦，我今天可不是要为了挑刺儿，毕竟任何项目都会存在这样那样的问题嘛，我今天要说的，只不过是从一个小小的功能点引申出来的代码实践而已，下面让我们进入正题… 


# Jide简介 

或许做Swing的各位同仁对jidesoft(http://www.jidesoft.com/)所提供的产品早就有所耳闻，也或许没有，总之俺是进花旗这个项目组之后才知道有这么一个产品（其实，从身边许多人那里都可以了解到，许多金融相关产品的客户端使用swing做的），你可以鄙视我，谁让俺之前只做过两年左右的swt/jface而没有用swing那，呵呵。 

应该说，jide提供的产品还是比较强大的，不过，商业产品，应该不是谁都愿意花这份儿银子吧！jide在原有swing基础上给出了多种扩展，包括dockable framework， action framework，dialogs，data grid/pivot table等等，我们今天要扯的话题与jide提供的WizardDialog有关，所以，就在这里对jide简单提及一下，希望没有说太多废话。 

# 现有codebase中的WizardDialog代码实践 

话说我们要为用户提供接口，分多步从用户那里获取输入数据，这种情况下，为用户提供一个向导式的输入界面应该说是比较自然的事情，现在，就有这样的一个场景，所以，我们决定使用jide提供的WizardDialog来实现之。 

《JIDE Dialogs Developer Guide》虽然对WizardDialog相关的各个类以及功能做了介绍，但是，如何在开发过程中使用它们，就得由我们来决定了，这自然就会引出不同的实践方式，下面是我从现有的codebase中移植过来的代码片段原型，我称之为“内部化”的代码实践（代码风格如何暂且不论）。 

首先，XAction类是一个标准的Swing Action，其定义如下： 

~~~~~~~ {.java}
XXXAction extends AbstractAction  
{  
    …  
    Public void actionPerformed(ActionEvent e)  
    {  
        // collect data from event object or outer references  
        ByBkWizard.showByBkDialog(trades);  
    }  
}  
~~~~~~~

当用户触发该Action对应的事件之后，定义的逻辑即被执行，简单点来说就是，搜集前提条件相关的数据，然后显示WizardDialog给用户，当用户输入完成之后，根据收集的输入结果进行后继处理，而这些工作，现在显然都是由这个ByBkWizard类来做了： 

~~~~~~~ {.java}
public class ByBkWizard  
{  
        public static void showByBkDialog(Collection<TransactionEntity> trades_) {  
            new ByBkWizard(trades_);  
        }  
  
        public ByBkWizard(Collection<TransactionEntity> trades_) {  
            _backupTrades = new HashMap<String, TransactionEntity>(trades_.size());  
            _trades = new ArrayList<TransactionEntity>(trades_.size());  
            for (TransactionEntity entity : trades_) {  
            try {  
                _backupTrades.put(getTempKey(entity), (TransactionEntity) entity.clone());  
                _trades.add((TransactionEntity) entity.clone());  
            } catch (Exception e) {  
                logger.error(e, e);  
            }  
initWizard();  
        }  
  
        // initWizard定义  
        PageList model = new PageList();  
  
        AbstractWizardPage setDataView = new BuyBackStepOneWizardPage();  
        AbstractWizardPage editView = new BuyBackStepTwoWizardPage();  
        model.append(setDataView);  
        model.append(editView);  
        wizard.setPageList(model);  
  
        wizard.addWindowListener(new WindowAdapter() {  
        public void windowClosing(final WindowEvent e) {  
            wizard.dispose();  
            _tradePanel.destroy();  
            _tradePanel = null;  
        }  
        });  
        wizard.setFinishAction(new AbstractAction("Send") {  
        private static final long serialVersionUID = 1L;  
  
        public void actionPerformed(final ActionEvent e) {  
            if (wizard.closeCurrentPage()) {  
            if (! checkInput()) {  
                GenericExceptionHandler.showOptionDialog("Warn",  
                    "The input is illegal for the callable trade(s)",  
                    JOptionPane.WARNING_MESSAGE);  
                return;  
            }  
            handleFinish();  
            wizard.setVisible(false);  
            wizard.dispose();  
            _tradePanel.destroy();  
            _tradePanel = null;  
            }  
        }  
        });  
        wizard.setCancelAction(new AbstractAction("Cancel") {  
        private static final long serialVersionUID = 1L;  
  
        public void actionPerformed(final ActionEvent e) {  
            if (wizard.closeCurrentPage()) {  
            wizard.dispose();  
            _tradePanel.destroy();  
            _tradePanel = null;  
            }  
        }  
        });  
          
           // 内部类声明  
           Class BuyBackStepOneWizardPage  
                      State, operations;  
           Class BuyBackStepTwoWizardPage  
                      State, operations;  
}  
~~~~~~~

OK, 从功能上来说，ByBkWizard完成了预期的业务处理需求，但是，这样的ByBkWizard定义是否合适，却有值得商榷的地方（看官，如果你觉得“只要功能完成了就得了呗！”，那么就到此为止吧，下面的话其实再看就没啥意思了）： 

1. 提供静态方法showByBkDialog的必要性是什么？！图方便？赶时髦？
2. 将各种逻辑都塞到一个类（ByBkWizard）里面不会觉得让这个类身材太“丰满”吗？怎么说也不符合当今的审美观吧？！界面显示的逻辑，模型数据处理逻辑要是给予独立的关注，是不是能让整个逻辑实现更加清晰一些那？！
3. 按理说这里的WizardDialog使用的各个Page确实也就这一个地方使用，将Page类定义为内部类也不算为过，而且，还能从ByBkWizard里直接访问这些Page的内部状态，多爽啊，不过，在我看来，更多的为是为了handleFinish（）这个方法服务吧？！
4. 而说到这个handleFinish()方法，也就引出了我最初所说的“内部化”实践的问题，对最终数据进行处理的逻辑“内部化”到了当前类当中，而不是剥离并封装为单独的实体，有错吗？！其实也没错，就是感觉不符合我的审美观，毕竟，DI和IoC提了这么些年，回过头来再看到这样的代码有些难以忍受罢了。

其实，往简单了说，所有的问题都归结为一点，即，代码职责不明确，或者专业点儿来说，关注点分离不够。而造成这种问题的前兆往往却是，不做足够的思考就根据功能去堆砌代码了。

# 重构后的WizardDialog代码实践 

现在让我们来重构这一功能实现，重构的主要目标包括： 

1. 重用WizardDialog的初始化逻辑；
2. 封装最终的数据处理逻辑；
3. 处理页面间的数据状态管理；

通常情况下，WizardDialog的使用流程是这样的： 

~~~~~~~ {.java}
AbstractWizardPage page1 = …;  
AbstractWizardPage page2= …;  
AbstractWizardPage page3 = …;  
…  
PageList model = new PageList();  
model .append(page1);  
model .append(page2);  
model .append(page3);  
…  
      
WizardSample wizard = new WizardSample("JIDE Wizard Demo");  
wizard.setPageList(model);  
wizard.setResizable(false);   
wizard.pack();  
wizard.setVisible(true); 
~~~~~~~

显然，除了使用的Pages不同，初始化一个WizardDialog并使用它的流程基本模式是不变的，那么我们就可以这一流程采用模板化封装，以期之后能够复用之，这也就有了如下模板类定义： 

~~~~~~~ {.java}
public class WizardDialogLauncher {  
      
    public void launch(String title,final PageList pageList,final Runnable finishAction)  
    {  
        launch(title,new IWizardDialogCustomizer(){  
            public void customizeWizardDialog(WizardDialog dialog) {  
                dialog.setPageList(pageList);  
                                dialog.setFinishAction(new AbstractAction("")  
                                {  
                                    ...  
                                    // finishAction.run();  
                                }  
                                ...// other settings  
            }});  
    }  
      
    public void launch(String title,IWizardDialogCustomizer customizer)  
    {  
        WizardDialog dialog = new WizardDialog(title);  
        customizer.customizeWizardDialog(dialog);  
        dialog.setVisible(true);  
    }  
}  

~~~~~~~

其中，IWizardDialogCustomizer为Callback接口，如果默认的模板方法不能满足需求的话，可以通过该Callback接口进一步定制WizardDialog。

现在第一个重构目标达成，而重构的过程中我们也引出了第二个重构目标，即封装最终的数据处理逻辑。当用户走完WizardDialog给出的数据输入请求流程之后，我们就应该对最终收集到的用户输入结果进行处理了，显然，不同的WizardDialog因为会根据需求而使用不同的Page，那么，对应的最终数据处理逻辑也是各异的，所以，我们不能将这些数据处理逻辑硬编码到我们的模板类当中，所以，我们暂且以Runnable的形式抽取出来，这样，每次启动新的WizardDialog的时候，只要根据需要，传入相应的Runnable形式封装的最终数据处理逻辑即可。（当然，你也可以以其它形式来封装最终的数据处理逻辑，只要能够保证这一逻辑可以被重用，并能够灵活处理） 

最后一个重构的目标是要处理页面间的数据状态管理，为什么要这么做那？！每一个WizardDialog都由多个WizardPage所组成，WizardDialog将这些Page来显示每一步的具体界面给用户，我们所要做的，只不过是通过PageList将这些Page交给WizardDialog进行显示罢了，至于这些Page管理则全都交由WizardDialog来做。但是，WizardDialog主要只负责这些页面之间的显示顺序以及最后向导结束后的行为定义，至于这些Page之间的数据状态管理，比如前后页面之间的数据依赖关系和传递，无论是《JIDE Dialogs Developer Guide》和相关类的Javadoc上都是只字未提，我想这就是为什么之前提到的实践方式中要将所有的数据状态以及页面逻辑都纳入一个类来管理的原因了，因为没有什么定规嘛，不过，这并不能为“内部化”的实践方式博得多少“同情”，实际上，我们完全可以将各个Page之间的模型数据以数据链的形式进行提供，比如，声明一个PagesModel，或者如果关心进一步的结构重用并不在乎数据类型检查的话，直接使用Map结构，让整个向导期间的数据交互都直接跟PagesModel或者Map形式的数据容器打交道，而之前的实践方式则基本上是通过同一个类定义范围内的状态共享来实现的。那么，我们是否要像之前的实践方式那样，做适当的变通，将状态定义为每一个Page的状态属性那？！稍微思考一下就会发现，这并不会带来什么好处，假设第二个Page要依赖第一个Page的数据状态，那么我们是不是要在初始化第二个Page的时候将第一个Page的引用传入那？如果第三个，第四个Page也有同样地状况那？！那就会纠缠不清了，还不如一了百了，让他们都直接跟某个统一的数据容器打交道来的清爽，我们所要所得，只不过是将同一个数据容器传给它们就是了。显然，作为数据容器的PagesModel或者Map可以在每一个Page中根据需要进行数据的获取和填充，比如页面初始化的时候获取数据容器中的数据初始化界面，而页面关闭的时候将页面上的某部分数据设置回数据容器，所有这些都可以通过为每个page注册相应的PageListener来完成，而且对每个Page都是独立的。 

当所有这些重构目标达成之后，我们的XAction定义基本上就是如下的样子：


~~~~~~~ {.java}
public class XAction extends AbstractAction {  
    private WizardDialogLauncher launcher;  
      
    public void actionPerformed(ActionEvent arg0) {  
        PagesModel model = new PagesModel();  
        model.setX(..);  
        ...  
        PageList pageList = new PageList();  
        pageList.append(new YourPageOne(model));  
        pageList.append(new YourPageTwo(model));  
        ...  
  
        Runnable finishAction = new YourFinishAction(model);  
          
        launcher.launch(pageList, finishAction);  
    }  
    // getters and setters  
}  
~~~~~~~

显然，如果有新的WizardDialog需求，我们所要做的，无非就是像如上代码所示，提供相应的Page实现和对应的FinishAction实现即可。 

重构的过程其实很简单，最主要在于，即使你不能使用Spring或者Guice之类的IoC容器或者框架，那也不应该放弃“外部化你的依赖管理”，没有框架的支持，不应该成为你不去编写可复用性，可测试性，可维护性良好的代码的借口。 

# 小结

1. 以旁观者的角度去观察，设计和实现你的代码逻辑，这将使你更加专注于明确各个实现类的职责，而不会被来自不同“地区”的依赖打乱“战局”。
2. 大家都知道，做同样一件事情有很多种方式，但是，总有更好的方式，如果你是一个追求完美的人，那即使没有任何的理论和设计模式之类的指导，你也同样可以写出令人赏心悦目地代码，因为，那是艺术，而你就是拥有艺术气质的那种人！









