import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.awt.Desktop;

public class NCM转换工具图形界面 extends JFrame {
    private JTextField 文件或目录路径输入框;
    private JTextField 输出目录输入框;
    private JCheckBox 递归处理选项;
    private JCheckBox 保留目录结构选项;
    private JTextArea 日志显示区;
    private JButton 选择文件或目录按钮;
    private JButton 选择输出目录按钮;
    private JButton 开始转换按钮;
    private JButton 帮助按钮;
    private JButton 版本信息按钮;
    private File ncmdump执行文件;
    private JProgressBar 进度条组件;

    public NCM转换工具图形界面() {
        setTitle("NCM文件转换工具（基于ncmdump）");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 增大窗口高度
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // 设置整体背景颜色为淡蓝色
        getContentPane().setBackground(new Color(220, 235, 245));

        提取ncmdump执行文件();

        // 创建主面板并设置边框和背景颜色
        JPanel 主面板 = new JPanel(new BorderLayout());
        主面板.setBorder(new EmptyBorder(15, 15, 15, 15));
        主面板.setBackground(new Color(240, 245, 250));

        // 顶部面板，包含文件/文件夹选择和输出目录选择
        JPanel 顶部面板 = new JPanel(new GridBagLayout());
        顶部面板.setBackground(new Color(240, 245, 250));
        GridBagConstraints 布局约束 = new GridBagConstraints();
        布局约束.gridx = 0;
        布局约束.gridy = 0;
        布局约束.anchor = GridBagConstraints.WEST;
        布局约束.insets = new Insets(5, 5, 5, 5);

        顶部面板.add(new JLabel("文件/文件夹路径:"), 布局约束);
        布局约束.gridx = 1;
        文件或目录路径输入框 = new JTextField(30);
        顶部面板.add(文件或目录路径输入框, 布局约束);
        布局约束.gridx = 2;
        选择文件或目录按钮 = new JButton("选择文件/文件夹");
        选择文件或目录按钮.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser 文件选择器 = new JFileChooser();
                文件选择器.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                文件选择器.setFileFilter(new FileNameExtensionFilter("NCM文件", "ncm"));
                int 选择结果 = 文件选择器.showOpenDialog(NCM转换工具图形界面.this);
                if (选择结果 == JFileChooser.APPROVE_OPTION) {
                    文件或目录路径输入框.setText(文件选择器.getSelectedFile().getAbsolutePath());
                }
            }
        });
        顶部面板.add(选择文件或目录按钮, 布局约束);

        布局约束.gridy++;
        布局约束.gridx = 0;
        顶部面板.add(new JLabel("输出目录:"), 布局约束);
        布局约束.gridx = 1;
        输出目录输入框 = new JTextField(30);
        顶部面板.add(输出目录输入框, 布局约束);
        布局约束.gridx = 2;
        选择输出目录按钮 = new JButton("选择输出目录");
        选择输出目录按钮.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser 目录选择器 = new JFileChooser();
                目录选择器.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int 选择结果 = 目录选择器.showOpenDialog(NCM转换工具图形界面.this);
                if (选择结果 == JFileChooser.APPROVE_OPTION) {
                    输出目录输入框.setText(目录选择器.getSelectedFile().getAbsolutePath());
                }
            }
        });
        顶部面板.add(选择输出目录按钮, 布局约束);

        // 中间面板，包含递归选项、保留结构选项和按钮
        JPanel 中间面板 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        中间面板.setBackground(new Color(240, 245, 250));
        递归处理选项 = new JCheckBox("递归处理");
        保留目录结构选项 = new JCheckBox("保留目录结构");
        开始转换按钮 = new JButton("开始转换");
        开始转换按钮.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                执行文件转换();
            }
        });
        帮助按钮 = new JButton("帮助");
        帮助按钮.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                显示帮助信息();
            }
        });
        版本信息按钮 = new JButton("版本信息");
        版本信息按钮.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                显示版本信息();
            }
        });

        中间面板.add(递归处理选项);
        中间面板.add(保留目录结构选项);
        中间面板.add(开始转换按钮);
        中间面板.add(帮助按钮);
        中间面板.add(版本信息按钮);

        // 底部日志面板
        日志显示区 = new JTextArea();
        日志显示区.setEditable(false);
        JScrollPane 滚动面板 = new JScrollPane(日志显示区);
        滚动面板.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 进度条
        进度条组件 = new JProgressBar(0, 100);
        进度条组件.setStringPainted(true);

        // 使用 GridBagLayout 来布局主面板，调整日志输出框的权重
        主面板.setLayout(new GridBagLayout());
        布局约束 = new GridBagConstraints();
        布局约束.gridx = 0;
        布局约束.gridy = 0;
        布局约束.fill = GridBagConstraints.HORIZONTAL;
        布局约束.weightx = 1.0;
        布局约束.weighty = 0.0;
        主面板.add(顶部面板, 布局约束);

        布局约束.gridy = 1;
        主面板.add(中间面板, 布局约束);

        布局约束.gridy = 2;
        布局约束.fill = GridBagConstraints.HORIZONTAL;
        布局约束.weighty = 0.0;
        主面板.add(进度条组件, 布局约束);

        布局约束.gridy = 3;
        布局约束.fill = GridBagConstraints.BOTH;
        布局约束.weighty = 1.0;
        主面板.add(滚动面板, 布局约束);

        add(主面板, BorderLayout.CENTER);
    }

    private void 提取ncmdump执行文件() {
        try {
            InputStream 输入流 = getClass().getResourceAsStream("/ncmdump.exe");
            if (输入流 == null) {
                JOptionPane.showMessageDialog(this, "未找到ncmdump.exe资源", "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            java.nio.file.Path 临时目录 = Files.createTempDirectory("ncmdump");
            ncmdump执行文件 = 临时目录.resolve("ncmdump.exe").toFile();
            try (OutputStream 输出流 = new FileOutputStream(ncmdump执行文件)) {
                byte[] 缓冲区 = new byte[4096];
                int 读取字节数;
                while ((读取字节数 = 输入流.read(缓冲区)) != -1) {
                    输出流.write(缓冲区, 0, 读取字节数);
                }
            }
            ncmdump执行文件.setExecutable(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "提取ncmdump.exe时出错: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void 执行文件转换() {
        String 源路径 = 文件或目录路径输入框.getText();
        String 输出路径 = 输出目录输入框.getText();
        boolean 启用递归 = 递归处理选项.isSelected();
        boolean 保留结构 = 保留目录结构选项.isSelected();

        if (源路径.isEmpty() || 输出路径.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择文件/文件夹和输出目录", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File 源文件或目录 = new File(源路径);
        List<String> 命令参数 = new ArrayList<>();
        命令参数.add(ncmdump执行文件.getAbsolutePath());

        int 文件总数 = 统计NCM文件数量(源文件或目录, 启用递归);
        进度条组件.setValue(0);
        进度条组件.setMaximum(文件总数);

        if (源文件或目录.isDirectory()) {
            命令参数.add("-d");
            命令参数.add(源路径);
            if (启用递归) {
                命令参数.add("-r");
            }
        } else if (源文件或目录.isFile() && 源路径.toLowerCase().endsWith(".ncm")) {
            命令参数.add(源路径);
        } else {
            JOptionPane.showMessageDialog(this, "选择的文件不是有效的NCM文件或选择的目录无效", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (保留结构) {
            // 处理保留目录结构
            try {
                Path 源基准路径 = Paths.get(源路径);
                递归处理目录结构(源基准路径, 源基准路径, Paths.get(输出路径), 启用递归, 0);
                日志显示区.append("转换完成（保留目录结构）\n");
                打开输出文件夹(输出路径);
                return;
            } catch (IOException e) {
                日志显示区.append("处理保留目录结构时出错: " + e.getMessage() + "\n");
                return;
            }
        }

        if (!输出路径.isEmpty()) {
            命令参数.add("-o");
            命令参数.add(输出路径);
        }

        try {
            ProcessBuilder 进程构建器 = new ProcessBuilder(命令参数);
            Process 进程 = 进程构建器.start();

            // 读取输出流和错误流
            new Thread(() -> 读取流(进程.getInputStream())).start();
            new Thread(() -> 读取流(进程.getErrorStream())).start();

            int 退出码 = 进程.waitFor();
            if (退出码 == 0) {
                日志显示区.append("转换完成\n");
                打开输出文件夹(输出路径);
            } else {
                日志显示区.append("转换失败，退出码: " + 退出码 + "\n");
            }
        } catch (IOException | InterruptedException e) {
            日志显示区.append("转换过程中出现错误: " + e.getMessage() + "\n");
        }
    }

    private int 统计NCM文件数量(File 文件或目录, boolean 启用递归) {
        if (文件或目录.isFile() && 文件或目录.getName().toLowerCase().endsWith(".ncm")) {
            return 1;
        } else if (文件或目录.isDirectory()) {
            int 数量 = 0;
            File[] 文件列表 = 文件或目录.listFiles();
            if (文件列表 != null) {
                for (File 文件 : 文件列表) {
                    if (启用递归 || 文件.isFile()) {
                        数量 += 统计NCM文件数量(文件, 启用递归);
                    }
                }
            }
            return 数量;
        }
        return 0;
    }

    private int 递归处理目录结构(Path 源基准路径, Path 当前源路径, Path 输出基准路径, boolean 启用递归, int 已处理文件数) throws IOException {
        File 当前源文件 = 当前源路径.toFile();
        if (当前源文件.isDirectory()) {
            if (启用递归) {
                File[] 文件列表 = 当前源文件.listFiles();
                if (文件列表 != null) {
                    for (File 文件 : 文件列表) {
                        已处理文件数 = 递归处理目录结构(源基准路径, 文件.toPath(), 输出基准路径, 启用递归, 已处理文件数);
                    }
                }
            }
        } else if (当前源文件.getName().toLowerCase().endsWith(".ncm")) {
            // 计算相对路径
            Path 相对路径 = 源基准路径.relativize(当前源路径);
            Path 输出路径;
            if (相对路径.getParent() != null) {
                输出路径 = 输出基准路径.resolve(相对路径.getParent());
            } else {
                输出路径 = 输出基准路径;
            }
            Files.createDirectories(输出路径);

            List<String> 命令参数 = new ArrayList<>();
            命令参数.add(ncmdump执行文件.getAbsolutePath());
            命令参数.add(当前源路径.toString());
            命令参数.add("-o");
            命令参数.add(输出路径.toString());

            try {
                ProcessBuilder 进程构建器 = new ProcessBuilder(命令参数);
                Process 进程 = 进程构建器.start();

                // 读取输出流和错误流
                new Thread(() -> 读取流(进程.getInputStream())).start();
                new Thread(() -> 读取流(进程.getErrorStream())).start();

                int 退出码 = 进程.waitFor();
                if (退出码 == 0) {
                    日志显示区.append("转换完成: " + 当前源路径 + "\n");
                } else {
                    日志显示区.append("转换失败: " + 当前源路径 + ", 退出码: " + 退出码 + "\n");
                }
            } catch (IOException | InterruptedException e) {
                日志显示区.append("转换过程中出现错误: " + 当前源路径 + ", 错误信息: " + e.getMessage() + "\n");
            }
            已处理文件数++;
            进度条组件.setValue(已处理文件数);
        }
        return 已处理文件数;
    }

    private void 读取流(InputStream 输入流) {
        try (BufferedReader 读者 = new BufferedReader(new InputStreamReader(输入流))) {
            String 行;
            while ((行 = 读者.readLine()) != null) {
                日志显示区.append(行 + "\n");
            }
        } catch (IOException e) {
            日志显示区.append("读取输出流时出现错误: " + e.getMessage() + "\n");
        }
    }

    private void 显示帮助信息() {
        String 帮助信息 = "使用方法：\n" +
                "1. 选择要转换的NCM文件或包含NCM文件的文件夹。\n" +
                "2. 选择输出目录。\n" +
                "3. 可选：勾选“递归处理”以递归处理文件夹中的所有NCM文件。\n" +
                "4. 可选：勾选“保留目录结构”以保持原目录结构输出转换后的文件。\n" +
                "5. 点击“开始转换”按钮进行转换。\n\n" +
                "注意事项：\n" +
                " - ncmdump.exe 已内置在程序中，无需手动放置。\n" +
                " - 网易云音乐3.0之后的某些版本，下载的ncm文件可能不内置封面图片，需要从网络获取。\n" +
                " - 本项目基于ncmdump，https://github.com/taurusxin/ncmdump";
        JOptionPane.showMessageDialog(this, 帮助信息, "帮助", JOptionPane.INFORMATION_MESSAGE);
    }

    private void 显示版本信息() {
        try {
            ProcessBuilder 进程构建器 = new ProcessBuilder(ncmdump执行文件.getAbsolutePath(), "-v");
            Process 进程 = 进程构建器.start();
            BufferedReader 读者 = new BufferedReader(new InputStreamReader(进程.getInputStream()));
            String 版本号 = 读者.readLine();
            JOptionPane.showMessageDialog(this, "ncmdump版本: " + 版本号, "版本信息", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            日志显示区.append("获取版本信息时出现错误: " + e.getMessage() + "\n");
        }
    }

    private void 打开输出文件夹(String 输出路径) {
        try {
            Desktop.getDesktop().open(new File(输出路径));
        } catch (IOException e) {
            日志显示区.append("无法打开输出文件夹: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NCM转换工具图形界面 界面 = new NCM转换工具图形界面();
            界面.setVisible(true);
        });
    }
}