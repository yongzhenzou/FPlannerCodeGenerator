package com.lh.android.code.generator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.lh.android.code.generator.GenerateLhCodeAction.FileType.ACTIVITY;

public class GenerateLhCodeAction extends AnAction {
    Project project;
    private String pageName;
    private String layoutName;
    private Boolean ifGenerateLayout;
    private String packageName;
    private boolean ifGenerateActivity;
    private boolean ifGenerateFragment;
    private boolean ifGenerateViewModel;
    private boolean ifGenerateDataBinding;
    private String rootPackageName;
    String modulePath;
    String layoutPath;
    String manifestPath;
    String daggerPath;
    String activityBindingModulePath;
    String fragmentBindingModulePath;
    String appViewModulePath;

    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getData(PlatformDataKeys.PROJECT);
        init();
        refreshProject();
    }

    private void refreshProject() {
        project.getBaseDir().refresh(false, true);
    }

    private void init() {
        ConfigDialog dialog = new ConfigDialog();
        dialog.setCallBack((pageName, rootPackageName, layoutName, ifGenerateLayout, packageName, ifGenerateActivity,
                            ifGenerateFragment, ifGenerateViewModel, ifGenerateDataBinding) -> {
            if (isEmpty(packageName)) {
                showMsg("输入类名 Page name");
                return;
            }
            if (isEmpty(rootPackageName)) {
                showMsg("输入类名 Root Package Name");
                return;
            }
            if (isEmpty(packageName)) {
                showMsg("输入类名 Package Name");
                return;
            }
            if (ifGenerateLayout && isEmpty(layoutName)) {
                showMsg("输入类名 Layout Name");
                return;
            }
            this.pageName = pageName;
            this.rootPackageName = rootPackageName;
            this.layoutName = layoutName;
            this.ifGenerateLayout = ifGenerateLayout;
            this.packageName = packageName;
            this.ifGenerateActivity = ifGenerateActivity;
            this.ifGenerateFragment = ifGenerateFragment;
            this.ifGenerateViewModel = ifGenerateViewModel;
            this.ifGenerateDataBinding = ifGenerateDataBinding;
            modulePath = getModulePath();
            layoutPath = getLayoutPath();
            manifestPath = project.getBasePath() + "/app/src/main/AndroidManifest.xml";
            daggerPath = project.getBasePath() + "/app/src/main/java/" + rootPackageName.replace(".", "/") + "/dagger";
            activityBindingModulePath = project.getBasePath() + "/app/src/main/java/" + rootPackageName.replace(".", "/") + "/dagger/ActivityBindingModule.kt";
            fragmentBindingModulePath = project.getBasePath() + "/app/src/main/java/" + rootPackageName.replace(".", "/") + "/dagger/FragmentBindingModule.kt";
            appViewModulePath = project.getBasePath() + "/app/src/main/java/" + rootPackageName.replace(".", "/") + "/dagger/appViewModelModule.kt";
            createClassFiles();
            Messages.showInfoMessage(project, "Success", "Code Generator");
        });
        dialog.setVisible(true);

    }

    void showMsg(String string) {
        Messages.showInfoMessage(project, string, "提示");
    }

    private void createClassFiles() {
        if (ifGenerateActivity) {
            createClassFile(ACTIVITY);
            createClassFile(FileType.MANIFEST);
            createClassFile(FileType.ACTIVITY_BINDDING_MODULE);
            createClassFile(FileType.VIEW_MODEL_MODULE);
        } else if (ifGenerateFragment) {
            createClassFile(FileType.FRAGMENT);
            createClassFile(FileType.FRAGMENT_BINDING_MODULE);
            createClassFile(FileType.VIEW_MODEL_MODULE);
        }
        if (ifGenerateLayout) {
            createClassFile(FileType.LAYOUT);
        }
        if (ifGenerateViewModel) {
            createClassFile(FileType.VIEWMODEL);
        }
        if (ifGenerateDataBinding) {
//            createClassFile(FileType.BINDDING_MODULE);
//            createClassFile(FileType.VIEWMODEL_MODULE);
        }
    }

    boolean isEmpty(String string) {
        return (string == null || string.isEmpty());
    }

    private void createClassFile(FileType fileType) {
        switch (fileType) {
            case ACTIVITY:
                File file = new File(modulePath + pageName + "Activity.kt");
                if (file.exists()) {
                    showMsg(file.getName() + "文件已存在");
                    return;
                }
                String activityContent = "";
                if (!ifGenerateViewModel&&!ifGenerateDataBinding){
                    activityContent = Utils.readPluginFile(this,"ActivityWithoutBindingAndViewModel.txt");
                }else if (!ifGenerateViewModel){
                    activityContent = Utils.readPluginFile(this,"ActivityWithoutViewModel.txt");
                }else if (!ifGenerateDataBinding){
                    activityContent = Utils.readPluginFile(this,"ActivityWithoutBinding.txt");
                }else {
                    activityContent = Utils.readPluginFile(this,"Activity.txt");
                }
                writeToFile(dealTempContent(activityContent, fileType),file);
                break;
            case VIEWMODEL:
                File viewModelFile = new File(modulePath + pageName + "ViewModel.kt");
                if (viewModelFile.exists()) {
                    showMsg(viewModelFile.getName() + "文件已存在");
                    return;
                }
                writeToFile(dealTempContent(Utils.readPluginFile(this,"ViewModel.txt"), fileType), viewModelFile);
                break;
            case FRAGMENT:
                File fragmentFile = new File(modulePath + pageName + "Fragment.kt");
                if (fragmentFile.exists()) {
                    showMsg(fragmentFile.getName() + "文件已存在");
                    return;
                }
                String fragmentContent = "";
                if (!ifGenerateViewModel&&!ifGenerateDataBinding){
                    fragmentContent = Utils.readPluginFile(this,"FragmentWithoutBindingAndViewModel.txt");
                }else if (!ifGenerateViewModel){
                    fragmentContent = Utils.readPluginFile(this,"FragmentWithoutViewModel.txt");
                }else if (!ifGenerateDataBinding){
                    fragmentContent = Utils.readPluginFile(this,"FragmentWithoutBinding.txt");
                }else {
                    fragmentContent = Utils.readPluginFile(this,"Fragment.txt");
                }
                writeToFile(dealTempContent(fragmentContent, fileType),fragmentFile);
                break;
            case LAYOUT:
                File layoutFile = new File(layoutPath + layoutName + ".xml");
                if (layoutFile.exists()) {
                    showMsg(layoutFile.getName() + "文件已存在");
                    return;
                }
                String layoutContent = "";
                if (!ifGenerateDataBinding){
                    layoutContent = Utils.readPluginFile(this,"LayoutWithoutBinding.txt");
                }else if (!ifGenerateViewModel){
                    layoutContent = Utils.readPluginFile(this,"LayoutWithoutViewModel.txt");
                }else {
                    layoutContent = Utils.readPluginFile(this,"Layout.txt");
                }
                writeToFile(dealTempContent(layoutContent, fileType), layoutFile);
                break;
            case MANIFEST:
                File manifestFile = new File(manifestPath);
                if (!manifestFile.exists()) {
                    manifestFile.mkdirs();
                    String manifestString = Utils.readPluginFile(this, "AndroidManifest.xml");
                    writeToFile(dealTempContent(manifestString, fileType), manifestFile);
                    return;
                }
                try {
                    String realManifestContent =Utils.readToString(manifestFile);
                    realManifestContent = realManifestContent.replace("\n" +
                            "    </application>", "\n" +
                            "        <activity\n" +
                            "            android:name=\".module." + pageName.toLowerCase() + "." + pageName + "Activity\"\n" +
                            "            android:screenOrientation=\"portrait\" />\n" +
                            "    </application>");
                    FileWriter writer = new FileWriter(manifestFile.getPath());
                    writer.write(realManifestContent);
                    writer.close();
                } catch (Exception e) {
                    showMsg(e.toString());
                }
                break;
            case ACTIVITY_BINDDING_MODULE:
                File bindModuleFile = new File(activityBindingModulePath);
                if (!bindModuleFile.exists()) {
                    try {
                        bindModuleFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String bindModuleString = Utils.readPluginFile(this, "ActivityBindingModule.txt");
                    writeToFile(dealTempContent(bindModuleString, fileType),bindModuleFile);
                    return;
                }
                try {
                    String realBindingModuleContent = Utils.readToString(bindModuleFile);
                    realBindingModuleContent = realBindingModuleContent.replace("@Module", "import " + packageName + "." + pageName + "Activity" +
                            "\n" +
                            "@Module");
                    realBindingModuleContent = realBindingModuleContent.replace("ActivityBindingModule {", "ActivityBindingModule {\n" +
                            "\n" +
                            "    @ActivityScoped\n" +
                            "    @ContributesAndroidInjector\n" +
                            "    abstract fun " + Utils.firstLetterLower(pageName) + "Activity(): " + pageName + "Activity");
                    //使用这个构造函数时，如果存在kuka.txt文件，
                    //则直接往kuka.txt中追加字符串
                    FileWriter writer = new FileWriter(bindModuleFile.getPath());
                    writer.write(realBindingModuleContent);
                    writer.close();
                } catch (Exception e) {
                    showMsg(e.toString());
                }
                break;
            case VIEW_MODEL_MODULE:
                if (!ifGenerateViewModel) return;
                File modelModuleFile = new File(appViewModulePath);
                if (!modelModuleFile.exists()) {
                    modelModuleFile.mkdirs();
                    String string = Utils.readPluginFile(this,"AppViewModelModule.txt");
                    writeToFile(dealTempContent(string, fileType), modelModuleFile);
                    return;
                }
                try {
                    String realModelModuleContent = Utils.readToString(modelModuleFile);
                    realModelModuleContent = realModelModuleContent.toString().replace("@Module", "import " + packageName + "." + pageName + "ViewModel" +
                            "\n" +
                            "@Module");
                    realModelModuleContent = realModelModuleContent.replace("AppViewModelModule {", "AppViewModelModule {\n" +
                            "    @Binds\n" +
                            "    @IntoMap\n" +
                            "    @ViewModelKey(" + pageName + "ViewModel::class)\n" +
                            "    abstract fun bind" + pageName + "ViewModel(viewModel: " + pageName + "ViewModel): ViewModel");
                    //使用这个构造函数时，如果存在kuka.txt文件，
                    //则直接往kuka.txt中追加字符串
                    FileWriter writer = new FileWriter(modelModuleFile.getPath());
                    writer.write(realModelModuleContent);
                    writer.close();
                } catch (Exception e) {
                    showMsg(e.toString());
                }
                break;
            case FRAGMENT_BINDING_MODULE:
                File fragmentBindModuleFile = new File(fragmentBindingModulePath);
                if (!fragmentBindModuleFile.exists()) {
                    fragmentBindModuleFile.mkdirs();
                    String string = Utils.readPluginFile(this,"FragmentBindingModule.txt");
                    writeToFile(dealTempContent(string, fileType), fragmentBindModuleFile);
                    return;
                }
                try {
                    String realBindingModuleContent = Utils.readToString(fragmentBindModuleFile);
                    realBindingModuleContent = realBindingModuleContent.toString().replace("@Module", "import " + packageName + "." + pageName + "Fragment" +
                            "\n" +
                            "@Module");
                    realBindingModuleContent = realBindingModuleContent.replace("FragmentBindingModule {", "FragmentBindingModule {\n" +
                            "\n" +
                            "    @FragmentScoped\n" +
                            "    @ContributesAndroidInjector\n" +
                            "    abstract fun " + Utils.firstLetterLower(pageName) + "Fragment(): " + pageName + "Fragment");
                    //使用这个构造函数时，如果存在kuka.txt文件，
                    //则直接往kuka.txt中追加字符串
                    FileWriter writer = new FileWriter(fragmentBindModuleFile.getPath());
                    writer.write(realBindingModuleContent);
                    writer.close();
                } catch (Exception e) {
                    showMsg(e.toString());
                }
                break;

        }
    }

    enum FileType {
        ACTIVITY, FRAGMENT, LAYOUT, VIEWMODEL, MANIFEST, ACTIVITY_BINDDING_MODULE, FRAGMENT_BINDING_MODULE, VIEW_MODEL_MODULE
    }

    /**
     *     * 替换模板中字符
     * <p>
     *     * @param content
     * <p>
     *     * @return
     * <p>
     *    
     */

    private String dealTempContent(String content, FileType fileType) {
        switch (fileType) {
            case ACTIVITY:
                if (!ifGenerateDataBinding) {
                    content = content.replace("\n" +
                            "import androidx.databinding.DataBindingUtil", "");
                    content = content.replace("\n" +
                            "import $rootPackageName.databinding.Activity$pageNameBinding", "");
                    content = content.replace("\n" +
                            "    private lateinit var binding: Activity$pageNameBinding", "");
                    content = content.replace("\n" +
                            "        setupBinding()", "");
                    content = content.replace("\n" +
                            "\n" +
                            "    private fun setupBinding() {\n" +
                            "        binding = DataBindingUtil.setContentView(this, R.layout.$layoutName)\n" +
                            "        binding.viewModel = viewModel\n" +
                            "        binding.lifecycleOwner = this\n" +
                            "    }", "");
                }
                if (!ifGenerateViewModel) {
                    content = content.replace("\n" +
                            "    private val viewModel by lazyViewModel<$pageNameViewModel>()", "");
                    content = content.replace("\n" +
                            "\n" +
                            "    private fun setupViewModel() {\n" +
                            "\n" +
                            "    }", "");
                    content = content.replace("\n" +
                            "\n" +
                            "    private fun setupViewModel() {\n" +
                            "\n" +
                            "    }", "");
                    if (ifGenerateDataBinding) {
                        content = content.replace("\n" +
                                "        binding.viewModel = viewModel", "");
                    }
                }
                break;
            case FRAGMENT:
                if (!ifGenerateDataBinding) {
                    content = content.replace("\n" +
                            "import androidx.databinding.DataBindingUtil", "");
                    content = content.replace("\n" +
                            "import $rootPackageName.databinding.Fragment$pageNameBinding", "");
                    content = content.replace("\n" +
                            "    private var binding: Fragment$pageNameBinding? = null", "");
                    content = content.replace("\n" +
                            "        setupBinding(view)", "");
                    content = content.replace("\n" +
                            "\n" +
                            "    private fun setupBinding(rootView: View) {\n" +
                            "        binding = DataBindingUtil.bind(rootView)\n" +
                            "        binding?.viewModel = viewModel\n" +
                            "        binding?.lifecycleOwner = this\n" +
                            "    }", "");
                }
                if (!ifGenerateViewModel) {
                    content = content.replace("\n" +
                            "    private val viewModel by lazyViewModel<$pageNameViewModel>()", "");
                    content = content.replace("\n" +
                            "        setupViewModel()", "");
                    content = content.replace("\n" +
                            "\n" +
                            "    private fun setupViewModel() {\n" +
                            "\n" +
                            "    }", "");
                    if (ifGenerateDataBinding) {
                        content = content.replace("\n" +
                                "        binding?.viewModel = viewModel", "");
                    }
                }
                break;

        }

        content = content.replace("$packageName", packageName);
        content = content.replace("$rootPackageName", rootPackageName);
        content = content.replace("$pageName", pageName);
        content = content.replace("$layoutName", layoutName);
        return content;

    }

    private String getModulePath() {
        String packagePath = packageName.replace(".", "/");
        String appPath = project.getBasePath() + "/app/src/main/java/" + packagePath + "/";
        return appPath;
    }

    private String getPackagePath() {
        String path = rootPackageName.replace(".", "/");
        String packagePath = project.getBasePath() + "/app/src/main/java/" + path + "/";
        return packagePath;
    }

    private String getLayoutPath() {
        String layoutPath = project.getBasePath() + "/app/src/main/res/layout/";
        return layoutPath;
    }


    /**
     *     * 获取当前时间
     * <p>
     *     * @return
     * <p>
     *    
     */

    public String getDate() {

        Date currentTime = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

        String dateString = formatter.format(currentTime);

        return dateString;

    }

    /**
     *     * 读取模板文件中的字符内容
     * <p>
     *     * @param fileName 模板文件名
     * <p>
     *     * @return
     * <p>
     *    
     */

    private String readTemplateFile(String fileName) {

        InputStream in = null;

        in = this.getClass().getResourceAsStream("/Template/" + fileName);

        String content = "";

        try {
            content = new String(readStream(in));
            in.close();
        } catch (IOException e) {
            e.printStackTrace();

        }

        return content;

    }

    private byte[] readStream(InputStream inputStream) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[2048];

        int len = -1;

        try {

            while ((len = inputStream.read(buffer)) != -1) {

                outputStream.write(buffer, 0, len);

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            outputStream.close();

            inputStream.close();

        }

        return outputStream.toByteArray();

    }

    /**
     *     * 生成
     * <p>
     *     * @param content 类中的内容
     * <p>
     *     * @param classPath 类文件路径
     * <p>
     *     * @param className 类文件名称
     * <p>
     *    
     */

    private void writeToFile(String content, File file) {

        try {
            if (!file.exists()) {
                file.createNewFile();

            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());

            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(content);

            bw.close();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    /**
     *     * 从AndroidManifest.xml文件中获取当前app的包名
     * <p>
     *     * @return
     * <p>
     *    
     */

    private String getPackageName() {

        String package_name = "";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(project.getBasePath() + "/App/src/main/AndroidManifest.xml");

            NodeList nodeList = doc.getElementsByTagName("manifest");

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                Element element = (Element) node;

                package_name = element.getAttribute("package");

            }

        } catch (Exception e) {
            showMsg("manifest操作失败");
            e.printStackTrace();

        }

        return package_name;

    }

}
