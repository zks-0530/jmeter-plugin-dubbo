package com.shouke.jmeter.plugin.dubbo.gui;

import com.shouke.jmeter.plugin.dubbo.sampler.DubboSampler;

import com.shouke.jmeter.plugin.dubbo.util.ClassUtil;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.log4j.Logger;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.EventObject;

import com.shouke.jmeter.plugin.dubbo.sampler.MethodArgument;

/**
 *  实现表格单元格内自动换行，设置单元格高度
 * */
class TableViewRenderer extends JTextArea implements TableCellRenderer
{
    public TableViewRenderer() {
        setLineWrap(true);      //将表格设为自动换行 //利用JTextArea的自动换行方法
        setWrapStyleWord(true); // 设置换行的时候不会造成断字的现象（允许比较长的单词换行到下一行:）
        setBorder(null);       //去掉JTextArea的默认边框，因为和JTable的表格线有重叠

    }

    public Component getTableCellRendererComponent(JTable jtable, Object obj, //obj指的是单元格内容
                                                   boolean isSelected, boolean hasFocus, int row, int column)
    {

        /**
         *  Object getValueAt(int rowIndex,int columnIndex)
         *  返回 columnIndex 和 rowIndex 位置的单元格值。
         *
         *  参数：
         *  rowIndex - 要查询的值所在行
         *  columnIndex - 要查询的值所在列
         *  返回：
         *  指定单元格位置的值 Object
         */
        //  计算当下行的最佳高度  //计算该行所有列的内容所对应的高度，挑选最高的那个
        int maxPreferredHeight = 30;
        for(int i = 0; i < jtable.getColumnCount(); i++) {
            setText("" + jtable.getValueAt(row, i));
            setSize(jtable.getColumnModel().getColumn(column).getWidth(), 0);
            maxPreferredHeight = Math.max(maxPreferredHeight, getPreferredSize().height);
        }

        if(jtable.getRowHeight(row) != maxPreferredHeight) {
            jtable.setRowHeight(row, maxPreferredHeight);
        }

        // 设置选中、非选中行的背景色
        if(isSelected) {
            this.setBackground(jtable.getSelectionBackground());
        } else {
            this.setBackground(jtable.getBackground());
        }

//        // 设置文本
        setText(obj == null ? "" : obj.toString()); //利用JTextArea的setText设置文本方法
//

        return this;
    }
}

/**
 *  实现单元格内回车换行编辑
 */
class TableCellTextAreaEditor extends AbstractCellEditor implements TableCellEditor{
    private JTextArea textArea;
    private JTable table;
    private int row;
    private int column;

    public TableCellTextAreaEditor(){
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        // TODO Auto-generated method stub
        textArea = new JTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setText((String)value);

        this.table = table;
        this.row = row;
        this.column = column;

        return textArea;
    }

    @Override
    public Object getCellEditorValue() {
        // TODO Auto-generated method stub

        return textArea.getText();
    }

    @Override
    public boolean stopCellEditing() {
        // TODO Auto-generated method stub
        super.stopCellEditing();
        return true;
    }

    @Override
    public void cancelCellEditing() {
        // TODO Auto-generated method stub

        super.cancelCellEditing();
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     *  重写isCellEditable方法，实现 仅双击鼠标才进入单元格编辑状态
     * @param eventobject
     * @return
     */
    @Override
    public boolean isCellEditable(EventObject eventobject) {
        if (eventobject instanceof MouseEvent) { // 如果为鼠标事件
            if (((MouseEvent) eventobject).getClickCount() >= 2) { // 鼠标双击事件
                return true; // 允许进入编辑
            }
        }
        return false;
    }

}


public class DubboSamplerGui extends AbstractSamplerGui {
    private JComboBox<String> registryTypeText;    // 注册中心类型
    private JTextField registryAddressText;        // 注册中心地址
    private int textColumns = 5;                   // 用于控制JTextField输入框的最小长度

    private JComboBox<String> rpcProtocolTypeText; // rpc协议类型
    private JTextField rpcDirectAddressText;       // rpc直连地址

    private JComboBox<String> packageText;         // JAR包名称
    private JComboBox<String> interfaceText;       // 接口名称
    private JComboBox<String> methodText;          // 方法名称
    private JTextField interfaceInputText;         // 接口名称(供手工输入使用)
    private JTextField methodInputText;            // 方法名称(供手工输入使用)

    private JTextField versionText;                // 接口服务版本
    private JTextField groupText;                  // 接口服务分组
    private JComboBox<String> loadbalanceText;     // 负载均衡策略
    private JComboBox<String> syncSettingText;     // 是否同步访问
    private JTextField connectionsText;            // 消费者连接数
    private JTextField timeoutText;                // 请求接口超时
    private JComboBox<String> clusterText;         // 集群容错模式
    private JTextField retriesText;                // 失败重试次数

    private static Logger logger = org.apache.log4j.Logger.getLogger(DubboSamplerGui.class);
    private DefaultTableModel model;
    private String[] columnNames = {"参数类型", "参数值"};
    private String[] tmpRow = {"", ""};

    private static List<String> packageNamesList = new ArrayList<>();     // 用于存放包列表列表（不包含绝对路径
    private static List<Object> interfaceNamesList = new ArrayList<>();  // 用于存放单个jar包的接口列表
    private static List<Object> methodNamesList = new ArrayList<>();     // 用于存放单个接口的方法列表
    private static HashMap<String, List<Object>> jarNamesMap = new HashMap<>();  // 存放每个jar包及对应名称的关系
    static {
        System.out.println();
        List<String> jarPkgsPathsList = null; // 存放jar包文件路径（包含绝对路径
        List<Object> jarPkgs = ClassUtil.getJarPkgs(System.getProperty("user.dir") + "/dubbo"); // 获取指定目录下的jar包文件
        if (jarPkgs.size() > 0) { // 如果结果不为空,即存在jar包
            jarPkgsPathsList = (List<String>)jarPkgs.get(0);
            packageNamesList = (List<String>)jarPkgs.get(1);
        }

        if (packageNamesList.size() == 0) {
            interfaceNamesList.add(new String[]{});
            methodNamesList.add(new String[]{});
        } else {
            HashMap<String,String[]> filtertMap = ClassUtil.getFilters(System.getProperty("user.dir") + "/dubbo/filter.propertities");
            List<Object>  jarNamesList;
            for (String jarPkgPath: jarPkgsPathsList) {
                String pkgName = "";   // 存放包名
                Integer index = jarPkgPath.lastIndexOf("\\");
                if (index > -1) {
                    pkgName = jarPkgPath.substring(index+1);
                } else {
                    continue;
                }

                try {
                    jarNamesList = ClassUtil.getNamesInJarPkg(jarPkgPath, pkgName, filtertMap);
                    if (jarNamesList != null) {
                        jarNamesMap.put(pkgName, jarNamesList);
                    }
                }catch (Exception e) {
                    logger.error(e.toString());

                }
            }

            String packageName = packageNamesList.get(0);
            if (jarNamesMap.containsKey(packageName)) {
                List<Object> nameList = jarNamesMap.get(packageName);
                HashMap<String, String[]> interfaceMap = (HashMap<String, String[]>)nameList.get(0);
                String[] interfaceNameArray = interfaceMap.get(packageName);

                interfaceNamesList.add(interfaceNameArray);

                HashMap<String, String[]> methodMap = (HashMap<String, String[]>)nameList.get(1);
                if (interfaceNameArray.length > 0) {
                    String[] methodNameArray = methodMap.get(packageName+interfaceNameArray[0]);
                    methodNamesList.add(methodNameArray);
                } else {
                    methodNamesList.add(new String[] {});
                }
            } else {
                interfaceNamesList.add( new String[] {});
                methodNamesList.add(new String[] {});
            }
        }
    }

    public DubboSamplerGui(){
        init();
        logger.debug("初始化GUI");
    }

    /**
     * 第一步 实现 getLabelResource() 该方法必须返回资源名称，代表组件的标题、名字
     */
    @Override
    public String getLabelResource() {
        logger.debug("请求getLabelResource");
        return this.getClass().getSimpleName();
//        return "Dubbo-Sampler";
    }

    /**
     * getStaticLabel 获取Sampler名称，如果缺少该函数，右键线程组->添加->Sampler，会看不到该组件
     */
    @Override
    public String getStaticLabel() {
        logger.debug("请求getStaticLabel");
        return "Dubbo Sampler";
    }


    /**
     * 第二步 创建GUI
     */
    public void init() {
        // 协议配置组框
//        该类 JPanel为轻量级组件提供通用容器，提供面板
        JPanel protocolSettings = new VerticalPanel();
        /**  setBorder()
         * 设置此组件的边框。 Border对象负责为组件定义插入(覆盖组件上直接设置的任何插入)，并可选地呈现这些插入边界内的任何边框装饰。 边框(而不是插图)应该被用来为摇摆组件创建装饰和非装饰区域(比如边距和填充)。 复合边框可用于在单个组件内嵌套多个边框。
         * 虽然从技术上讲，您可以在继承自JComponent的任何对象上设置边界，但是许多标准Swing组件的外观实现并不能很好地使用用户设置的边界。 通常，当您希望在JPanel或JLabel以外的标准Swing组件上设置边框时，我们建议您将组件放在JPanel中，并在JPanel上设置边框。
         * 这是一个绑定属性。
         * 参数:
         * Border—要为该组件呈现的边框
         * 参见:
         * 边界,CompoundBorder
         *
         * BorderFactory()
         * 用于出售标准Border对象的工厂类。只要有可能，这个工厂将提供对共享Border实例的引用。要了解更多的信息和示例，请参见Java教程中如何使用边框的一节。
         *createTitledBorder()
         * 使用指定的标题、默认边框类型(由当前外观和感觉决定)、默认文本位置(由当前外观和感觉决定)、默认对齐(前导)以及默认字体和文本颜色(由当前外观和感觉决定)创建一个新的标题边框。
         * title -一个包含标题文本的字符串
         * 返回:TitledBorder对象
         *
         */
        protocolSettings.setBorder(BorderFactory.createTitledBorder("协议配置"));

        JPanel registrySettingsHP = new HorizontalPanel(5,0.5F); // 注册中心配置水平面板 //组件之间的横向距离设置为 5
        // 注册中心类型    JLabel:标签组件
//        SwingConstants.RIGHT 一组常量，通常用于在屏幕上定位和定位组件，框方向常数，用于指定框的右侧
        JLabel registryTypeLabel = new JLabel("注册中心类型：", SwingConstants.RIGHT);
        registryTypeText = new JComboBox<String>(new String[] {"zookeeper://", "multicast://", "redis://", "simple://", "none"});

        registryTypeText.setPreferredSize(new Dimension(100, 25)); // 设置ComboBox尺寸
        registryTypeLabel.setLabelFor(registryTypeText);
        JLabel registryTypeHelpLable = new JLabel();
        registryTypeHelpLable.setIcon(new ImageIcon(getClass().getResource("/images/help.png")));
        registryTypeHelpLable.setToolTipText("直连Dubbo应用服务器请选择\"none\"");

        // 添加到 “协议配置水平面板”
        registrySettingsHP.add(registryTypeLabel);
        registrySettingsHP.add(registryTypeText);
        registrySettingsHP.add(registryTypeHelpLable);

        // 注册中心地址
        JLabel registryAddressLabel = new JLabel("     注册中心地址：", SwingConstants.RIGHT);
        registryAddressText = new JTextField();
        registryAddressLabel.setLabelFor(registryAddressText);
        protocolSettings.add(registryAddressLabel);

        registrySettingsHP.add(registryAddressLabel);
        registrySettingsHP.add(registryAddressText);
        protocolSettings.add(registrySettingsHP); // 添加到 注册中心配置组框

        //**************************************************
        JPanel rpcProtocolSettingsHP = new HorizontalPanel(5,0.5F);

        // RPC协议类型
        JLabel rpcProtocolTypeLabel = new JLabel("RPC协议类型：", SwingConstants.RIGHT);
        rpcProtocolTypeText = new JComboBox<String>(new String[] {"dubbo://", "rmi://", "hessian://", "webservice://", "memcached://", "redis://"});
        rpcProtocolTypeText.setPreferredSize(new Dimension(100, 25));
        rpcProtocolTypeLabel.setLabelFor(rpcProtocolTypeText);

        rpcProtocolSettingsHP.add(rpcProtocolTypeLabel);
        rpcProtocolSettingsHP.add(rpcProtocolTypeText);


        // RPC调用地址
        JLabel rpcInvokeAddressLabel = new JLabel("             RPC直连地址：", SwingConstants.RIGHT);
        rpcDirectAddressText = new JTextField();
        rpcInvokeAddressLabel.setLabelFor(rpcDirectAddressText);
        JLabel rpcInvokeAddressHelpLable = new JLabel();
        rpcInvokeAddressHelpLable.setIcon(new ImageIcon(getClass().getResource("/images/help.png")));
        rpcInvokeAddressHelpLable.setToolTipText("直连地址：不经过注册中心，直接访问应用的地址");

        rpcProtocolSettingsHP.add(rpcInvokeAddressLabel);
        rpcProtocolSettingsHP.add(rpcDirectAddressText);
        rpcProtocolSettingsHP.add(rpcInvokeAddressHelpLable);
        protocolSettings.add(rpcProtocolSettingsHP);

        //**************************************************
        //接口设置组框
        JPanel interfaceSettings = new VerticalPanel();
        interfaceSettings.setBorder(BorderFactory.createTitledBorder("接口设置"));

        JPanel  packageHP =  new HorizontalPanel(5,0.5F);
        // 包名称
        JLabel packageLabel = new JLabel("包名称：", SwingConstants.RIGHT);

        packageText = new JComboBox<>(packageNamesList.toArray(new String[packageNamesList.size()]));

//        packageText.setPreferredSize(new Dimension(0, 25));
        packageLabel.setLabelFor(packageText);
        packageText.addActionListener(new ActionListener() {
            @Override
            public  void actionPerformed(ActionEvent arg0) {
                if (interfaceText != null && methodText != null) {
                    interfaceText.removeAllItems();
                    methodText.removeAllItems();

                    String packageName = packageText.getSelectedItem().toString();

                    if (jarNamesMap.containsKey(packageName)) {
                        List<Object> nameList = jarNamesMap.get(packageName);
                        HashMap<String, String[]> interfaceMap = (HashMap<String, String[]>)nameList.get(0);
                        if (interfaceMap.containsKey(packageName)) {
                            String[] interfaceNameArray = interfaceMap.get(packageName);

                            for (String interfaceName: interfaceNameArray) {
                                interfaceText.addItem(interfaceName);
                            }

                            HashMap<String, String[]> methodMap = (HashMap<String, String[]>)nameList.get(1);
                            if (interfaceNameArray.length > 0) {
                                String key = packageName+interfaceNameArray[0];
                                if (methodMap.containsKey(key)) {
                                    String[] methodNameArray = methodMap.get(key);

                                    for (String methodName: methodNameArray) {
                                        methodText.addItem(methodName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        packageHP.add(packageLabel);
        packageHP.add(packageText);
        interfaceSettings.add(packageHP);

        JPanel  interfaceHP =  new HorizontalPanel(5,0.5F);
        // 接口名称
        JLabel interfaceLabel = new JLabel("接口名称：", SwingConstants.RIGHT);

        interfaceText = new JComboBox<>((String[]) interfaceNamesList.get(0));
//        logger.debug("(String[]) interfaceNamesList.get(0)" + Arrays.toString((String[]) interfaceNamesList.get(0)));

//        interfaceText.setPreferredSize(new Dimension(0, 25));
        interfaceLabel.setLabelFor(interfaceText);
        interfaceText.addActionListener(new ActionListener() {
            @Override
            public  void actionPerformed(ActionEvent arg0) {
                methodText.removeAllItems();
                String packageName = packageText.getSelectedItem().toString();
                if (interfaceText.getSelectedItem() != null) {
                    String interfaceName = interfaceText.getSelectedItem().toString();

                    if (jarNamesMap.containsKey(packageName)) {
                        List<Object> nameList = jarNamesMap.get(packageName);
                        HashMap<String, String[]> methodMap = (HashMap<String, String[]>)nameList.get(1);
                        String key = packageName+interfaceName;
                        if (methodMap.containsKey(key)) {
                            String[] interfaceNameArray = methodMap.get(key);

                            for (String methodName: interfaceNameArray) {
                                methodText.addItem(methodName);
                            }
                        }
                    }
                }
            }
        });

        interfaceHP.add(interfaceLabel);
        interfaceHP.add(interfaceText);
        interfaceSettings.add(interfaceHP);


        JPanel methodHP =  new HorizontalPanel(5,0.5F);
        // 方法名称
        JLabel methodLabel = new JLabel("方法名称：", SwingConstants.RIGHT);
//        methodText = new JComboBox<String>(new String[] {"queryContainersByContainerNo"});
        methodText = new JComboBox<String>((String[]) methodNamesList.get(0));
//        interfaceText.setPreferredSize(new Dimension(0, 25));
        methodLabel.setLabelFor(methodText);

        methodHP.add(methodLabel);
        methodHP.add(methodText);
        interfaceSettings.add(methodHP);

        JPanel  interfaceInputHP =  new HorizontalPanel(5,0.5F);
        // 接口名称，供手工输入使用
        JLabel interfaceInputLabel = new JLabel("接口名称：", SwingConstants.RIGHT);
        interfaceInputText = new JTextField(textColumns);
        interfaceInputText.setPreferredSize(new Dimension(100, 25)); // 设置输入框尺寸
        interfaceInputLabel.setLabelFor(interfaceInputText);

        JLabel interfaceInputHelperLabel = new JLabel();
        interfaceInputHelperLabel.setIcon(new ImageIcon(getClass().getResource("/images/help.png")));
        interfaceInputHelperLabel.setToolTipText("说明：如果不想、无法通过下拉方式选择目标接口，请在此手工输入");

        interfaceInputHP.add(interfaceInputLabel);
        interfaceInputHP.add(interfaceInputText);
        interfaceInputHP.add(interfaceInputHelperLabel);
        interfaceSettings.add(interfaceInputHP);

        JPanel  methodInputHP =  new HorizontalPanel(5,0.5F);
        // 方法名称，供手工输入使用
        JLabel methodInputLabel = new JLabel("方法名称：", SwingConstants.RIGHT);
        methodInputText = new JTextField(textColumns);
        methodInputText.setPreferredSize(new Dimension(100, 25)); // 设置输入框尺寸
        methodInputLabel.setLabelFor(methodInputText);

        JLabel methodInputHelperLabel = new JLabel();
        methodInputHelperLabel.setIcon(new ImageIcon(getClass().getResource("/images/help.png")));
        methodInputHelperLabel.setToolTipText("说明：如果不想、无法通过下拉方式选择目标方法，请在此手工输入");

        methodInputHP.add(methodInputLabel);
        methodInputHP.add(methodInputText);
        methodInputHP.add(methodInputHelperLabel);
        interfaceSettings.add(methodInputHP);


        //**************************************************
        JPanel consumerSettingsHP1 = new HorizontalPanel(5,0.5F);
        JPanel consumerSettingsHP2 = new HorizontalPanel(5,0.5F);

        // 请求接口版本
        JLabel versionLabel = new JLabel("请求接口版本：", SwingConstants.RIGHT);
        versionText = new JTextField(textColumns);
        versionLabel.setLabelFor(versionText);

        consumerSettingsHP1.add(versionLabel);
        consumerSettingsHP1.add(versionText);

        // 接口服务分组
        JLabel groupLabel = new JLabel("     接口服务分组：", SwingConstants.RIGHT);
        groupText = new JTextField(textColumns);
        versionLabel.setLabelFor(groupText);

        consumerSettingsHP1.add(groupLabel);
        consumerSettingsHP1.add(groupText);

        // 负载均衡策略
        JLabel loadbalanceLabel = new JLabel("     负载均衡策略：", SwingConstants.RIGHT);
        loadbalanceText = new JComboBox<String>(new String[] {"random", "roundrobin", "leastactive", "consistenthash"});
        loadbalanceText.setPreferredSize(new Dimension(120, 25));
        loadbalanceLabel.setLabelFor(loadbalanceText);
        JLabel loadbalanceHelperLable = new JLabel();
        loadbalanceHelperLable.setIcon(new ImageIcon(getClass().getResource("/images/help.png")));
        loadbalanceHelperLable.setToolTipText("\"random\"：按权重随机调用，\"roundrobin\"：轮询，\"leastactive\"：最少活跃次数，\"consistenthash\"：一致性hash");

        consumerSettingsHP1.add(loadbalanceLabel);
        consumerSettingsHP1.add(loadbalanceText);
        consumerSettingsHP1.add(loadbalanceHelperLable);

        // 同步、异步请求设置
        JLabel syncSettingLabel = new JLabel("     是否同步请求：", SwingConstants.RIGHT);
        syncSettingText = new JComboBox<String>(new String[] {"True", "False"});
        syncSettingText.setPreferredSize(new Dimension(100, 20));
        loadbalanceLabel.setLabelFor(syncSettingText);

        consumerSettingsHP1.add(syncSettingLabel);
        consumerSettingsHP1.add(syncSettingText);

        // 消费者连接数
        JLabel connectionsLabel = new JLabel("消费者连接数：", SwingConstants.RIGHT);
        connectionsText = new JTextField(textColumns);
        versionLabel.setLabelFor(connectionsText);

        JLabel connectionsHelperLable = new JLabel();
        connectionsHelperLable.setIcon(new ImageIcon(getClass().getResource("/images/help.png")));
        connectionsHelperLable.setToolTipText("参考网络说明：0-表示该服务使用JVM共享长连接；1-表示该服务使用独立长连接；2-表示该服务使用独立两条长连接，以此类推。 请谨慎配置，如果无特殊需求，建议设置默认值 0。");


        consumerSettingsHP2.add(connectionsLabel);
        consumerSettingsHP2.add(connectionsText);
        consumerSettingsHP2.add(connectionsHelperLable);

        // 请求接口超时
        JLabel timeoutLabel = new JLabel("     请求接口超时(毫秒)：", SwingConstants.RIGHT);
        timeoutText = new JTextField(textColumns);
//        timeoutText.setText("5000"); // 默认超时时间 5s
        timeoutLabel.setLabelFor(timeoutText);

        consumerSettingsHP2.add(timeoutLabel);
        consumerSettingsHP2.add(timeoutText);


        // 集群容错模式
        JLabel clusterLabel = new JLabel("     集群容错模式：", SwingConstants.RIGHT);
        clusterText = new JComboBox<String>(new String[] {"failover", "failfast", "failsafe", "failback", "forking", "broadcast"});
        clusterText.setPreferredSize(new Dimension(100, 25));
        clusterLabel.setLabelFor(clusterText);

        consumerSettingsHP2.add(clusterLabel);
        consumerSettingsHP2.add(clusterText);

        // 失败重试次数
        JLabel retriesLabel = new JLabel("     失败重试次数：", SwingConstants.RIGHT);
        retriesText = new JTextField(textColumns);
        retriesText.setText("0"); // 默认超时时间 5s
        retriesLabel.setLabelFor(timeoutText);

        consumerSettingsHP2.add(retriesLabel);
        consumerSettingsHP2.add(retriesText);
        interfaceSettings.add(consumerSettingsHP1);
        interfaceSettings.add(consumerSettingsHP2);

        // 请求参数组框
        JPanel methodArgSettings = new VerticalPanel();
        methodArgSettings.setBorder(BorderFactory.createTitledBorder("请求参数"));

//        选项卡模式
//        JPanel tablePanel = new HorizontalPanel();
//        ImageIcon ii = new ImageIcon("/images/help.gif");
//        JTabbedPane jTabbedpane = new JTabbedPane();
//        jTabbedpane.addTab("选项卡1", ii, tablePanel, "first");
//        JPanel tablePane2 = new HorizontalPanel();
//        jTabbedpane.addTab("选项卡2", ii, tablePane2, "second");
//        jTabbedpane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
//        jTabbedpane.setTabPlacement(JTabbedPane.TOP);    //设置标签停放的位置
//        methodArgSettings.add(jTabbedpane);

        // 表格参数面板
        JPanel tablePanel = new HorizontalPanel();

        model = new DefaultTableModel();
        model.setDataVector(null, columnNames); // 设置表头名称
        final JTable table = new JTable(model);
//        table.setRowHeight(30); // 设置行高
        table.setDefaultRenderer(Object.class, new TableViewRenderer());     // 设置JTable单元格默认渲染器
        table.setDefaultEditor(Object.class, new TableCellTextAreaEditor()); // 设置JTable单元格默认编辑器
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); // 失去光标退出编辑


        //表格滚动条
        JScrollPane scrollpane = new JScrollPane(table){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 400); // 设置滚动条大小，以增加表格展示面积
            }
        };


        tablePanel.add(scrollpane);
        methodArgSettings.add(tablePanel);

        JPanel btnPanel = new HorizontalPanel();
        btnPanel.setLayout(new FlowLayout()); //流式布局
        // 添加按钮
        JButton addBtn = new JButton("增加");
        addBtn.addActionListener(new ActionListener() {
           @Override
           public  void actionPerformed(ActionEvent arg0) { model.addRow(tmpRow);}
        });

        // 删除按钮
        JButton delBtn = new JButton("删除");
        delBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int rowIndex = table.getSelectedRow();
                if(rowIndex != -1) {
                    model.removeRow(rowIndex);
                } else {
                    int rows = table.getRowCount();
                    if (rows >= 1) {
                        rowIndex = rows - 1;
                        model.removeRow(rowIndex);
                    }
                }
            }
        });

        btnPanel.add(delBtn, FlowLayout.CENTER);
        btnPanel.add(addBtn, FlowLayout.CENTER);
        methodArgSettings.add(btnPanel);

        interfaceSettings.add(methodArgSettings);

        //设置总panel，垂直布局
        JPanel settingPanel = new VerticalPanel();
        settingPanel.setBorder(makeBorder());
        settingPanel.add(makeTitlePanel());

        settingPanel.add(protocolSettings);
        settingPanel.add(interfaceSettings);


        //全局布局设置
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(settingPanel, BorderLayout.CENTER);
    }


    // 转换 Vector<Vector<String>>  为Vector<MethodArgument>
    private Vector<MethodArgument> parseMethodArguments1(Vector<Vector<String>> methodArgs) {
        Vector<MethodArgument> methodArguments = new Vector<>();
        if (!methodArgs.isEmpty()) {
            //处理参数
            Iterator<Vector<String>> it = methodArgs.iterator();
            while(it.hasNext()) {
                Vector<String> methodArgument = it.next();
                if (!methodArgument.isEmpty()) {
                    methodArguments.add(new MethodArgument(methodArgument.get(0), methodArgument.get(1)));
                }
            }
        }
        return methodArguments;
    }

    // 转换 Vector<MethodArgument>  为  Vector<Vector<String>>
    private Vector<Vector<String>> parseMethodArguments2(Vector<MethodArgument> methodArgs) {
        Vector<Vector<String>> methodArguments = new Vector<>();
        if (!methodArgs.isEmpty()) {
            //处理参数
            Iterator<MethodArgument> it = methodArgs.iterator();
            while(it.hasNext()) {
                MethodArgument methodArgument = it.next();
                if (methodArgument != null) {
                    Vector<String> vector = new Vector<>();
                    vector.add(methodArgument.getArgumentType());
                    vector.add(methodArgument.getArgumentValue());
                    methodArguments.add(vector);
                }
            }
        }
        return methodArguments;
    }


    /**
     * 第三步 实现 public void configure(TestElement el) //Use this method to set data into your GUI elements
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element); // 必不可少

        logger.debug("获取DubboSampler对象属性值赋值给GUI");
        DubboSampler sampler = (DubboSampler) element;
        registryTypeText.setSelectedItem(sampler.getRegistryType());
        registryAddressText.setText(sampler.getRegistryAddress());

        rpcProtocolTypeText.setSelectedItem(sampler.getRpcProtocolType());
        rpcDirectAddressText.setText(sampler.getRpcAddress());

        packageText.setSelectedItem(sampler.getPackage());
        interfaceText.setSelectedItem(sampler.getInterface1());
        interfaceInputText.setText(sampler.getInterface2());
        methodText.setSelectedItem(sampler.getMethod1());
        methodInputText.setText(sampler.getMethod2());
        versionText.setText(sampler.getVersion());
        groupText.setText(sampler.getGroup());
        loadbalanceText.setSelectedItem(sampler.getLoadbalance());
        syncSettingText.setSelectedItem(sampler.getSync());
        connectionsText.setText(sampler.getConnections());
        timeoutText.setText(sampler.getTimeout());
        clusterText.setSelectedItem(sampler.getCluster());
        retriesText.setText(sampler.getRetries());

        // 设置表格数据
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("参数类型");
        columnNames.add("参数值");
        model.setDataVector(parseMethodArguments2(sampler.getMethodArguments()), columnNames);
    }


    /**
     * 第四步 实现 public void modifyTestElement(TestElement e)
     */
    @Override
    public void modifyTestElement(TestElement te) {
        te.clear();
        configureTestElement(te); // 调用该方法提供一些默认数据
        logger.debug("把GUI数据赋值给sampler");
        DubboSampler sampler = (DubboSampler) te;
        sampler.setRegistryType(registryTypeText.getSelectedItem().toString());
        sampler.setRegistryAddress(registryAddressText.getText());

        sampler.setRpcProtocolType(rpcProtocolTypeText.getSelectedItem().toString());
        sampler.setRpcAddress(rpcDirectAddressText.getText());

        if (packageText.getItemCount() > 0) {
            sampler.setPackage(packageText.getSelectedItem().toString());
        } else {
            sampler.setPackage("");
        }

        if (interfaceText.getItemCount() > 0) {
            sampler.setInterface1(interfaceText.getSelectedItem().toString());
        } else {
            sampler.setInterface1("");
        }

        sampler.setInterface2(interfaceInputText.getText());
        if (methodText.getItemCount() > 0) {
            sampler.setMethod1(methodText.getSelectedItem().toString());
        } else {
            sampler.setMethod1("");
        }

        sampler.setMethod2(methodInputText.getText());
        sampler.setVersion(versionText.getText());
        sampler.setGroup(groupText.getText());
        sampler.setLoadbalance(loadbalanceText.getSelectedItem().toString());
        sampler.setSync(syncSettingText.getSelectedItem().toString());
        sampler.setConnections(connectionsText.getText());
        sampler.setTimeout(timeoutText.getText());
        sampler.setCluster(clusterText.getSelectedItem().toString());
        sampler.setRetries(retriesText.getText());

        sampler.setMethodArguments(parseMethodArguments1(model.getDataVector()));

    }

    /**
     * 第五步 实现 public TestElement createTestElement() // 该方法应该创建一个TestElement 类实例
     * 然后把该实例传递给上述创建的 modifyTestElement(TestElement) 方法
     */
    @Override
    public TestElement createTestElement() {
        logger.debug("创建DubboSampler对象");
        DubboSampler sampler = new DubboSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void clearGui() {
        super.clearGui();
        logger.debug("清空GUI数据");
        registryTypeText.setSelectedIndex(0);
        registryAddressText.setText("");

        rpcProtocolTypeText.setSelectedIndex(0);
        rpcDirectAddressText.setText("");

        if (packageText.getItemCount() > 0) {
            packageText.setSelectedIndex(0);
        }

        if (interfaceText.getItemCount() > 0) {
            interfaceText.setSelectedIndex(0);
        }

        interfaceInputText.setText("");

        if (methodText.getItemCount() > 0) {
            methodText.setSelectedIndex(0);
        }
        methodInputText.setText("");

        versionText.setText("");
        groupText.setText("");
        loadbalanceText.setSelectedIndex(0);
        syncSettingText.setSelectedIndex(0);
        connectionsText.setText("0");
        timeoutText.setText("5000");
        clusterText.setSelectedIndex(0);
        retriesText.setText("0");
        model.setDataVector(null, columnNames);
    }
}
