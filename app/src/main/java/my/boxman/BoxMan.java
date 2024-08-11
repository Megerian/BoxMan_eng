package my.boxman;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.method.NumberKeyListener;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class BoxMan extends Activity implements mySplitLevelsFragment.SplitStatusUpdate, myQueryFragment.FindStatusUpdate, myExportFragment.ExportStatusUpdate {

	private static final int REQUEST_PERMISSIONS = 1;
	private static final int REQUEST_MANAGE_EXTERNAL_STORAGE = 2;

	ExpandableListView expView;
	MyExpandableListView expAdapter;
	long exitTime = 0; //两次返回键退出，计算时间间隔
	int m_nItemSelect;  //对话框中的出item选择前的记忆

	int groupPos;  //长按条目的位置
	int childPos;
	boolean andOpen = false;  //导入后是否允许打开关卡

	private static String url = "api/competition/";
	private static String url_Num;
	private ProgressDialog dialog;

	private mySplitLevelsFragment mDialog;
	private myQueryFragment mDialog2;
	private myExportFragment mDialog3;
	private static final String TAG_PROGRESS_DIALOG_FRAGMENT = "split_progress_fragment";

	//一级 item
	private String[] groups;

	myMaps.MyAdapter mAdapter;
	ArrayList<Long> mySets;   //关卡集id


	// Android 11 and higher
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_MANAGE_EXTERNAL_STORAGE) {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
				if (Environment.isExternalStorageManager()) {
					continueWithPermissions();
				} else {
					// Permission denied, inform the user
					Toast.makeText(this, "Program cannot run without Manage External Storage permission!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	// Android below 11
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode == REQUEST_PERMISSIONS){
			if(grantResults.length > 0){
				boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
				boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

				if(read && write){
					continueWithPermissions();
				}else{
					Toast.makeText(this, "Program cannot run without WRITE_EXTERNAL_STORAGE permission!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	//建立地图列表类
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
			if (!Environment.isExternalStorageManager()) {
				// Request the MANAGE_EXTERNAL_STORAGE permission
				Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
				startActivityForResult(intent, REQUEST_MANAGE_EXTERNAL_STORAGE);
			} else {
				continueWithPermissions();		// Permission is already granted
			}
		} else {
			// For devices below Android 11, request READ/WRITE permissions
			if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
					ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
						REQUEST_PERMISSIONS);
			} else {
				continueWithPermissions();		// Permission is already granted
			}
		}
	}

	void continueWithPermissions() {

		//路径设置
//		myMaps.sRoot = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath(); // this folder is deleted when the app is uninstalled and the user can't add files to it.
//		myMaps.sRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
		myMaps.sRoot = Environment.getExternalStorageDirectory().getPath();

		myMaps.sPath = new StringBuilder("/推箱快手/").toString();

		myMaps.m_Settings = new int[44]; //系统参数设置数组 | Array of settings
		loadSettings();  //读入系统设置
        myMaps.myPathList[0] = myMaps.sPath + "关卡图/";

		setLocale();
		myMaps.res = getResources();

		groups = new String[] { getString(R.string.beginner_puzzles), getString(R.string.advanced_puzzles), getString(R.string.tricky_puzzles), getString(R.string.additional_puzzles) };
		setContentView(R.layout.main);


		//取得 Context
		try {
			myMaps.ctxDealFile = this.createPackageContext("my.boxman", Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e1) {
			//
		}

		mySQLite.m_SQL = mySQLite.getInstance(this);
		if (!mySQLite.m_SQL.openDataBase()){
			MyToast.showToast(this, getString(R.string.error_in_program), Toast.LENGTH_SHORT);
			finish();
			System.exit(0);
		} else {
			//检查关卡库（DB）版本是否符合要求
			int[] mVer_Array = {923};  // DB V9.23 对关卡库进行了综合性的升级和更新，基本上可以取代之前所有版本的升级
			List<Integer> Ver_List = mySQLite.m_SQL.Get_DB_Ver(mVer_Array);
			int len2 = Ver_List.size();
			if (len2 > 0) {  //需要先做 DB 升级
				StringBuilder inf = new StringBuilder();

				int k = 0, ver;
				while (k < len2) {
					ver = Ver_List.get(k++);
					inf.append("Update ").append((ver / 100)).append('.').append(String.format("%02d", ver % 100)).append('\n');
				}
				inf.append(getString(R.string.db_update_necessary));

				AlertDialog.Builder builder4 = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
				builder4.setTitle(R.string.alertTitle_attention).setMessage(inf.toString())
						.setPositiveButton(getString(R.string.okay), new OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								mySQLite.m_SQL.closeDataBase();
								finish();
								System.exit(0);
							}
						}).setCancelable(false).show();
			}

//			long ssss = System.currentTimeMillis();
			setTitle(getString(R.string.app_name) + " " + mySQLite.m_SQL.count_Level());
//			MyToast.showToast(this, ""+(System.currentTimeMillis() - ssss), Toast.LENGTH_LONG);
		}

		//在这里检查游戏外置目录建立情况
		InputStream fis = null;
		FileOutputStream fos = null;
		try {
			File targetDir = new File(myMaps.sRoot+myMaps.sPath + "皮肤/");
			if (!targetDir.exists()) targetDir.mkdirs();  //创建自定义皮肤文件夹

			File skinFile = new File(myMaps.sRoot+myMaps.sPath + "皮肤/defskin.png");
			if (!skinFile.exists()) {  //复制系统默认皮肤，供玩家参考
				fis = getResources().openRawResource(R.raw.defskin);
				fos = new FileOutputStream(skinFile);

				int len = fis.available();
				byte[] buffer = new byte[len];
				while (fis.read(buffer) > 0) fos.write(buffer);
				myMaps.skin_File = "defskin.png";
			}
			//复制背景图片，供玩家参考
			targetDir = new File(myMaps.sRoot+myMaps.sPath + "背景/");
			if (!targetDir.exists()) targetDir.mkdirs();  //创建背景文件夹

			skinFile = new File(myMaps.sRoot+myMaps.sPath + "背景/net.jpg");
			if (!skinFile.exists()) {
				fis = getResources().getAssets().open("net.jpg");
				fos = new FileOutputStream(skinFile);

				int len = fis.available();
				byte[] buffer = new byte[len];
				while (fis.read(buffer) > 0) fos.write(buffer);
			}
			skinFile = new File(myMaps.sRoot+myMaps.sPath + "背景/flower.jpg");
			if (!skinFile.exists()) {
				fis = getResources().getAssets().open("flower.jpg");
				fos = new FileOutputStream(skinFile);

				int len = fis.available();
				byte[] buffer = new byte[len];
				while (fis.read(buffer) > 0) fos.write(buffer);
			}
			targetDir = new File(myMaps.sRoot+myMaps.sPath + "超长答案/"); // Solutions
			if (!targetDir.exists()) targetDir.mkdirs();  //创建文件夹
			targetDir = new File(myMaps.sRoot+myMaps.sPath + "导入/");    // Import
			if (!targetDir.exists()) targetDir.mkdirs();  //创建文件夹
			targetDir = new File(myMaps.sRoot+myMaps.sPath + "导出/");	   // Export
			if (!targetDir.exists()) targetDir.mkdirs();  //创建文件夹
			targetDir = new File(myMaps.sRoot+myMaps.sPath + "创编关卡/"); // Create puzzles
			if (!targetDir.exists()) targetDir.mkdirs();  //创建文件夹
			targetDir = new File(myMaps.sRoot+myMaps.sPath + "关卡图/");  // Puzzle maps
			if (!targetDir.exists()) targetDir.mkdirs();  //创建文件夹
			targetDir = new File(myMaps.sRoot+myMaps.sPath + "宏/");	  // Macros
			if (!targetDir.exists()) targetDir.mkdirs();  //创建文件夹
		} catch (Exception e) { } finally {
			try {
				if (fis != null) fis.close();
				if (fos != null) fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//二级 item
		myMaps.mSets0 = mySQLite.m_SQL.get_GroupList(0);
		myMaps.mSets1 = mySQLite.m_SQL.get_GroupList(1);
		myMaps.mSets2 = mySQLite.m_SQL.get_GroupList(2);
		myMaps.mSets3 = mySQLite.m_SQL.get_GroupList(3);
		//若扩展关卡组是空的，则自动创建一个“新关卡集”
		if (myMaps.mSets3.size() == 0) {
			try {
				long new_ID = mySQLite.m_SQL.add_T(3, getString(R.string.new_puzzle_set), "", "");
				//将“新关卡集”加入列表
				set_Node nd = new set_Node();
				nd.id = new_ID;
				nd.title = getString(R.string.new_puzzle_set);
				myMaps.mSets3.add(nd);
			} catch (Exception e) { }
		}

		//对扩展关卡集，按名称排序
		MyComparator mc = new MyComparator() ;
		Collections.sort(myMaps.mSets3, mc) ;

		//开启标题栏的返回键
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);

		expView = findViewById(R.id.explist);
		expAdapter = new MyExpandableListView();
		expView.setAdapter(expAdapter);

		//打开上次的位置
		if (myMaps.m_Settings[0] >= 0 && myMaps.m_Settings[0] < expView.getExpandableListAdapter().getGroupCount()) {
			expView.expandGroup(myMaps.m_Settings[0]);
		}

		//设置item点击的监听器
		expView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
										int groupPosition, int childPosition, long id) {

				browLevels(groupPosition, childPosition);

				return true;
			}
		});

		expView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				groupPos = (Integer)(arg1.getTag(R.id.tag_g));
				childPos = (Integer)(arg1.getTag(R.id.tag_c));

				if (childPos >= 0)
					expView.showContextMenu();

				return true;
			}

		});
		registerForContextMenu(expView);

		// 屏幕宽高（PX）
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		myMaps.m_nWinWidth = metric.widthPixels;  //屏幕尺寸，此值将在加载背景图片时使用
		myMaps.m_nWinHeight = metric.heightPixels;

		myMaps.loadSkins();  //加载皮肤
		myMaps.loadBKPic();  //背景图片
		loadGifSkins();      //加载 Gif 皮肤
	}

	private void setLocale() {
		if(Objects.equals(myMaps.localCode, "??")) {
			Locale locale;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				locale = getResources().getConfiguration().getLocales().get(0);
			} else {
				locale = getResources().getConfiguration().locale;
			}
			myMaps.localCode = locale.getLanguage();
		}

		Locale locale = new Locale(myMaps.localCode);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.setLocale(locale);
		getResources().updateConfiguration(config, getResources().getDisplayMetrics());
	}

	//加载 Gif 皮肤
	private void loadGifSkins() {
		if (myMaps.skinGif != null) myMaps.skinGif.recycle();
		InputStream fis = myMaps.res.openRawResource(R.raw.gifskin);
		myMaps.skinGif = BitmapFactory.decodeStream(fis);

		//默认的 GIF 水印
		if (myMaps.markGif1 != null) myMaps.markGif1.recycle();
		InputStream fis2 = myMaps.res.openRawResource(R.raw.banner5c);
		myMaps.markGif1 = BitmapFactory.decodeStream(fis2);

		//自定义的 GIF 水印
		if (myMaps.markGif2 != null) myMaps.markGif2.recycle();
		try{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inPreferredConfig = myMaps.cfg;
			opts.inJustDecodeBounds = true;  //仅提取图片宽高

			String bannerFileName = myMaps.sRoot+myMaps.sPath + "banner.png";
			if(new File(bannerFileName).exists()) {
				BitmapFactory.decodeFile(bannerFileName, opts);
				//建议使用 200 x 42 尺寸内的水印
	//			if (opts.outWidth <= 200 && opts.outHeight <= 42) {
					opts.inJustDecodeBounds = false;   //提取图片
					myMaps.markGif2 = BitmapFactory.decodeFile(bannerFileName, opts);
	//			}
			}
		} catch (Exception e) {
			myMaps.markGif2 = null;
		}

		System.gc();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	final NumberKeyListener getNumber = new NumberKeyListener() {
		@Override
		protected char[] getAcceptedChars() {
			return new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
		}

		@Override
		public int getInputType() {
			return InputType.TYPE_CLASS_PHONE;
		}
	};

	public boolean onOptionsItemSelected(MenuItem mt) {
		switch (mt.getItemId()) {
			case R.id.menu_set:  //导入
				myMaps.m_Set_id = -1;  //需要创建新的关卡集
				sel_Set();

				return true;
			case R.id.menu_exp_ans:  //导出
				sel_Set2();

				return true;
			case R.id.menu_NewSet:  //增设新的关卡集...
				final EditText et = new EditText(this);
				et.setText(myMaps.getNewSetName());
				et.setMaxLines(1);
				et.selectAll();    //.setSelection(et.getText().length());

				new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK).setTitle(R.string.puzzle_set_name).setCancelable(false)
						.setView(et).setOnKeyListener(new DialogInterface.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface di, int keyCode, KeyEvent event) {
						if(keyCode == KeyEvent.KEYCODE_ENTER){
							String input;
							try {
								input = et.getText().toString().trim();

								if (input.equals("")) {
									MyToast.showToast(BoxMan.this, getString(R.string.invalid_name) + input, Toast.LENGTH_SHORT);
								} else {
									if (mySQLite.m_SQL.find_Set(input, myMaps.mSets3.get(0).id) > 0) {
										MyToast.showToast(BoxMan.this, getString(R.string.this_name_already_exists) + input, Toast.LENGTH_SHORT);
										et.setText(input);
										et.setSelection(input.length());
									} else {
										try {
											//创建新的关卡集
											long new_ID = mySQLite.m_SQL.add_T(3, input, "", "");
											//将新关卡集加入列表
											set_Node nd = new set_Node();
											nd.id = new_ID;
											nd.title = input;
											myMaps.mSets3.add(1, nd);
											expAdapter.notifyDataSetChanged();
											MyToast.showToast(BoxMan.this, getString(R.string.new_puzzle_set_successfully_created) + input, Toast.LENGTH_SHORT);
											di.dismiss();
											return true;
										} catch (Exception e) {
											MyToast.showToast(BoxMan.this, getString(R.string.toast_unknown_reason_creation_failed), Toast.LENGTH_SHORT);
										}
									}
								}
							} catch (Exception e) {
								MyToast.showToast(BoxMan.this, getString(R.string.check_name_is_legal), Toast.LENGTH_SHORT);
							}
						}
						return false;
					}
				}).setPositiveButton(getString(R.string.okay), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String input;
						try {
							input = et.getText().toString().trim();

							if (input.equals("")) {
								MyToast.showToast(BoxMan.this, getString(R.string.invalid_name) +"\n" + input, Toast.LENGTH_SHORT);
							} else {
								if (myMaps.mSets3.size() > 0 && mySQLite.m_SQL.find_Set(input, myMaps.mSets3.get(0).id) > 0) {
									MyToast.showToast(BoxMan.this, getString(R.string.this_name_already_exists) + "\n" + input, Toast.LENGTH_SHORT);
									et.setText(input);
									et.setSelection(input.length());
								} else {
									try {
										//创建新的关卡集
										long new_ID = mySQLite.m_SQL.add_T(3, input, "", "");
										//将新关卡集加入列表
										set_Node nd = new set_Node();
										nd.id = new_ID;
										nd.title = input;
										myMaps.mSets3.add(nd);
										expAdapter.notifyDataSetChanged();
										MyToast.showToast(BoxMan.this,getString(R.string.new_puzzle_set_successfully_created) + "\n" + input, Toast.LENGTH_SHORT);
									} catch (Exception e) {
										MyToast.showToast(BoxMan.this, getString(R.string.toast_unknown_reason_creation_failed), Toast.LENGTH_SHORT);
									}
								}
							}
						} catch (Exception e) {
							MyToast.showToast(BoxMan.this, getString(R.string.check_name_is_legal), Toast.LENGTH_SHORT);
						}
					}})
						.setNegativeButton(getString(R.string.cancel), null)
						.show();
				return true;
			case R.id.menu_builder:
				//确保关卡集进入一次 （点击太快，可能进入两次）
				if (myMaps.curJi) return true;
				myMaps.curJi = true;

				//加载“创编关卡”文件夹中以前创建的关卡
				myMaps.read_DirBuilder();

				//启动关卡列表视图
				Intent intent0 = new Intent();
				intent0.setClass(this, myGridView.class);
				startActivity(intent0);

				return true;
			case R.id.menu_recent:  //最近推过的关卡
				//确保关卡集进入一次 （点击太快，可能进入两次）
				if (myMaps.curJi) return true;
				myMaps.curJi = true;

				//加载地图集
				mySQLite.m_SQL.get_Recent();

				if (myMaps.m_lstMaps.size() < 1){
					MyToast.showToast(this, getString(R.string.toast_puzzle_not_found), Toast.LENGTH_SHORT);
					myMaps.curJi = false;
				} else {
					myMaps.m_Settings[0] = 0;
					myMaps.m_Settings[1] = 0;
					myMaps.m_Set_id = -1;
					myMaps.sFile = "最近推过的关卡";
					myMaps.J_Title = myMaps.sFile;
					myMaps.J_Author = "";
					myMaps.J_Comment = "";
					//启动关卡列表视图
					Intent intent1 = new Intent();
					intent1.setClass(this, myGridView.class);
					startActivity(intent1);
				}
				return true;
			case R.id.menu_query:  //关卡查询
				View view = View.inflate(this, R.layout.query_dialog, null);
				final CheckBox m_All = (CheckBox) view.findViewById(R.id.query_all);      //全选
				final CheckBox m_Ans = (CheckBox) view.findViewById(R.id.query_ans);      //搜索答案库
				myMaps.m_setName = (ListView) view.findViewById(R.id.query_setname);      //关卡集选项
				final EditText l_title = (EditText) view.findViewById(R.id.level_title);  //关卡名称
				final EditText l_author = (EditText) view.findViewById(R.id.level_author);  //关卡作者
				final EditText l_boxs = (EditText) view.findViewById(R.id.level_boxs);  //关卡箱子数
				final EditText l_cols = (EditText) view.findViewById(R.id.level_cols);  //关卡列数
				final EditText l_rows = (EditText) view.findViewById(R.id.level_rows);  //关卡行数
				final EditText l_boxs2 = (EditText) view.findViewById(R.id.level_boxs2);  //关卡箱子数
				final EditText l_cols2 = (EditText) view.findViewById(R.id.level_cols2);  //关卡列数
				final EditText l_rows2 = (EditText) view.findViewById(R.id.level_rows2);  //关卡行数
				l_boxs.setKeyListener(getNumber);
				l_boxs2.setKeyListener(getNumber);
				l_cols.setKeyListener(getNumber);
				l_rows.setKeyListener(getNumber);
				l_cols2.setKeyListener(getNumber);
				l_rows2.setKeyListener(getNumber);
				mySets = new ArrayList<Long>();                                                            //关卡集 id
				mAdapter = new myMaps.MyAdapter(this, (ArrayList)myMaps.getData(mySets));
				myMaps.m_setName.setAdapter(mAdapter);                                                     //关卡集
				myMaps.m_setName.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);                             //开启多选模式
				for (int k = 0; k < myMaps.m_setName.getCount(); k++) {
					myMaps.m_setName.setItemChecked(k, true);
				}
				myMaps.m_setName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						mySets.set(position, -mySets.get(position));

						if (mySets.get(position) > 0) myMaps.m_setName.setItemChecked(position, true);
						else myMaps.m_setName.setItemChecked(position, false);

						//点击item后，通知adapter重新加载view
						mAdapter.notifyDataSetChanged();
					}
				});

				m_All.setChecked(true);                                                               //是否全选
				m_All.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						for (int k = 0; k < myMaps.m_setName.getCount(); k++) {
							myMaps.m_setName.setItemChecked(k, isChecked);
							if (isChecked) {
								if (mySets.get(k) < 0) mySets.set(k, -mySets.get(k));
							} else {
								if (mySets.get(k) > 0) mySets.set(k, -mySets.get(k));
							}
						}
					}
				});

				AlertDialog.Builder dlg = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
				dlg.setView(view).setTitle(R.string.puzzle_query).setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.okay), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String m_title = l_title.getText().toString().trim();
						String m_author = l_author.getText().toString().trim();
						String m_boxs = l_boxs.getText().toString().trim();
						String m_cols = l_cols.getText().toString().trim();
						String m_rows = l_rows.getText().toString().trim();
						String m_boxs2 = l_boxs2.getText().toString().trim();
						String m_cols2 = l_cols2.getText().toString().trim();
						String m_rows2 = l_rows2.getText().toString().trim();
						int boxs, cols, rows, boxs2, cols2, rows2;
						try {
							boxs = Integer.parseInt(m_boxs);
						} catch (Exception e) {
							boxs = 0;
						}
						try {
							cols = Integer.parseInt(m_cols);
						} catch (Exception e) {
							cols = 0;
						}
						try {
							rows = Integer.parseInt(m_rows);
						} catch (Exception e) {
							rows = 0;
						}
						try {
							boxs2 = Integer.parseInt(m_boxs2);
						} catch (Exception e) {
							boxs2 = 0;
						}
						try {
							cols2 = Integer.parseInt(m_cols2);
						} catch (Exception e) {
							cols2 = 0;
						}
						try {
							rows2 = Integer.parseInt(m_rows2);
						} catch (Exception e) {
							rows2 = 0;
						}

						if (!m_title.isEmpty() || !m_author.isEmpty() || boxs > 0 || cols > 0 || rows > 0 || boxs2 > 0 || cols2 > 0 || rows2 > 0) {

							int len = 0;
							for (int k = 0; k < mySets.size(); k++) {
								if (mySets.get(k) > 0) len++;
							}
							if (len > 0 || m_Ans.isChecked()) {  //选择了关卡集
								long[] m_sets = null;

								if (len != mySets.size()) {  //全选时，数组为 null
									m_sets = new long[len];
									int i = 0;
									for (int k = 0; k < mySets.size(); k++) {
										if (mySets.get(k) > 0) m_sets[i++] = mySets.get(k);
									}
								}

								if (mDialog2 == null) {
									mDialog2 = new myQueryFragment();
									Bundle bundle = new Bundle();
									bundle.putLongArray("mSets", m_sets);
									bundle.putBoolean("mAns", m_Ans.isChecked());
									bundle.putString("mTitle", m_title);
									bundle.putString("mAauthor", m_author);
									bundle.putInt("mBoxs", boxs);
									bundle.putInt("mCols", cols);
									bundle.putInt("mRows", rows);
									bundle.putInt("mBoxs2", boxs2);
									bundle.putInt("mCols2", cols2);
									bundle.putInt("mRows2", rows2);

									mDialog2.setArguments(bundle);
									mDialog2.show(getFragmentManager(), TAG_PROGRESS_DIALOG_FRAGMENT);
								}
							}
						}
					}
				}).setCancelable(false).create().show();
				return true;
			case R.id.menu_submit_list:
				//比赛答案提交列表
				Intent intent3 = new Intent();
				intent3.setClass(this, mySubmitList.class);
				startActivity(intent3);

				return true;
			case R.id.menu_recog:
				myMaps.edPicList(myMaps.sRoot + myMaps.myPathList[myMaps.m_Settings[36]]);
				Intent intent4 = new Intent();
				intent4.setClass(this, myPicListView.class);
				startActivity(intent4);

				return true;
			case R.id.menu_help:
				//帮助
				Intent intent1 = new Intent(this, Help.class);
				//用Bundle携带数据
				Bundle bundle = new Bundle();
				bundle.putInt("m_Num", 0);  //传递参数，指示调用者
				intent1.putExtras(bundle);

				intent1.setClass(this, Help.class);
				startActivity(intent1);

//				Intent intent1 = new Intent();
//				intent1.setClass(this, Help.class);
//				startActivity(intent1);

				return true;

			case R.id.menu_language:
				//语言设置 | change language
				AlertDialog.Builder languageSelection = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
				String[] languages = new String[] { getString(R.string.chinese), getString(R.string.english) };
				int checkedItem = myMaps.localCode.equalsIgnoreCase("zh") ? 0 : 1;

				final String[] selectedLanguageLocaleCode = { myMaps.localCode };

				languageSelection.setTitle(getString(R.string.language))
						.setSingleChoiceItems(languages, checkedItem, (languageDialog, selectedLanguageIndex) ->
							selectedLanguageLocaleCode[0] = (selectedLanguageIndex == 0) ? "zh" : "en"
						)
						.setPositiveButton(getString(R.string.okay), (languageDialog, selectedButton) -> {
							myMaps.localCode = selectedLanguageLocaleCode[0];	// set new language code in the settings
							setLocale();										// Set new locale for the app
							saveSets();											// Save the new locale in the settings file

							Intent intent = new Intent(this, BoxMan.class);		// Restart the activity with new locale
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						})
						.setNegativeButton(getString(R.string.cancel), null);
				languageSelection.setCancelable(false).show();

				return true;


			case R.id.menu_about:
//			if (mySQLite.m_SQL.exp_Inf_ALL())
//				MyToast.showToast(BoxMan.this, "导出成功！", Toast.LENGTH_SHORT);
				//关于
				Intent intent2 = new Intent();
				intent2.setClass(this, myAbout.class);
				startActivity(intent2);

				return true;
			default:
				return super.onOptionsItemSelected(mt);
		}
	}

	//扩展关卡集按关卡集名称（标题）排序
	class MyComparator implements Comparator {
		public int compare(Object o1,Object o2) {

			//把名称中的汉字换成拼音
			String s1 = ((set_Node)o1).title.trim();
			String s2 = ((set_Node)o2).title.trim();

			String s3 = s1.substring(0,1);
			String s4 = s2.substring(0,1);
			boolean f1 = s3.compareTo("\u4e00")>0 && s3.compareTo("\u9fa5")<0;  //首字符是否为汉字
			boolean f2 = s4.compareTo("\u4e00")>0 && s4.compareTo("\u9fa5")<0;  //首字符是否为汉字

			if (!f1 && f2)
				return -1;
			else if (f1 && !f2)
				return 1;
			else if (!f1 && !f2) {
				if (s1.compareToIgnoreCase(s2) < 0)
					return -1;
				else if (s1.compareToIgnoreCase(s2) > 0)
					return 1;
				else
					return 0;
			} else {
				try {
					s3 = new String(s1.getBytes("GB2312"),"ISO-8859-1");
					s4 = new String(s2.getBytes("GB2312"),"ISO-8859-1");
					return s3.compareTo(s4);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		}
	}

	//加载快手设置
	private void loadSettings() {
		File f = new File(myMaps.sRoot+myMaps.sPath + "BoxMan.ini");
		if (!f.exists()) {
			myMaps.m_Settings[38] = 1;   //区分奇偶地板格
			myMaps.m_Settings[11] = 1;   //死锁提示
			myMaps.m_Settings[8]  = 1;   //显示可达提示
			myMaps.m_Settings[27] = 1;   //仓管员转向动画
			myMaps.m_Settings[3]  = 1;   //长按目标点提示关联网点及网口
			myMaps.m_Settings[37] = 1;   //打开关卡是自动加载最新状态
			myMaps.m_Settings[17] = 1;   //允许穿越
			myMaps.m_Settings[29] = 1;   //自动爬阶梯
			myMaps.m_Settings[25] = 1;   //推关卡时显示当前时间提示

			myMaps.m_Settings[12] = 1;   //标识重复关卡
			myMaps.m_Settings[10] = 3;   //移动速度
			myMaps.m_Settings[22] = 1;   //编辑关卡图时，哪些元素携带标尺
			myMaps.m_Settings[30] = 1;   //导出答案的注释信息
			myMaps.m_Settings[31] = 1;   //导入关卡为一个时，自动打开
			myMaps.m_Settings[40] = 5;   //奇位地板格明暗度

			myMaps.skin_File = "默认皮肤";
			myMaps.bk_Pic = "使用背景色";
			myMaps.nickname = "";
			myMaps.uil = "https://sokoban.cn/";
			myMaps.country = "00";
			myMaps.email = "";
			myMaps.mMatchNo = "";
			myMaps.mMatchDate1 = "";
			myMaps.mMatchDate2 = "";
			myMaps.localCode = "??";  // => use phone language
			return;
		}

		IniFile file = new IniFile();
		file.load(f);

		myMaps.m_Settings[37] = 1;         // 打开无解关卡时，自动加载最新状态
		myMaps.m_Settings[0] = Integer.parseInt(file.get("常规", "当前关卡集组别", "0").toString());
		myMaps.m_Settings[1] = Integer.parseInt(file.get("常规", "当前关卡集", "0").toString());
		myMaps.m_Settings[2] = Integer.parseInt(file.get("常规", "预览时是否显示关卡标题", "0").toString());
		myMaps.m_Settings[12] = Integer.parseInt(file.get("常规", "是否标识出重复关卡", "1").toString());
		myMaps.m_Settings[33] = Integer.parseInt(file.get("常规", "浏览时每行的图标数", "0").toString());
		myMaps.m_Settings[34] = Integer.parseInt(file.get("常规", "浏览时每行的图标默认数", "0").toString());
		myMaps.localCode = file.get("常规", "语言设置", "ZH").toString(); // Language: ZH = Chinese, EN = English

		myMaps.m_Settings[4] = Integer.parseInt(file.get("界面", "背景色", "0").toString());
		myMaps.skin_File = file.get("界面", "皮肤", "默认皮肤").toString();
		myMaps.bk_Pic = file.get("界面", "背景图片", "使用背景色").toString();
		myMaps.m_Settings[25] = Integer.parseInt(file.get("界面", "背景时间", "1").toString());

		myMaps.m_Settings[6] = Integer.parseInt(file.get("速度", "瞬移状态", "0").toString());
		myMaps.m_Settings[10] = Integer.parseInt(file.get("速度", "移动速度", "3").toString());

		myMaps.m_Settings[11] = Integer.parseInt(file.get("操作", "是否提示死锁", "1").toString());
		myMaps.m_Settings[3] = Integer.parseInt(file.get("操作", "长按点位提示关联网", "0").toString());
		myMaps.m_Settings[8] = Integer.parseInt(file.get("操作", "显示可达提示", "1").toString());
		myMaps.m_Settings[27] = Integer.parseInt(file.get("操作", "仓管员转向动画", "1").toString());
		myMaps.m_Settings[20] = Integer.parseInt(file.get("操作", "禁用全屏", "0").toString());
		myMaps.m_Settings[28] = Integer.parseInt(file.get("操作", "演示时仅推动", "0").toString());
		myMaps.m_Settings[9] = Integer.parseInt(file.get("操作", "标尺不随关卡旋转", "0").toString());
		myMaps.m_Settings[15] = Integer.parseInt(file.get("操作", "是否允许音量键选择关卡", "0").toString());
		myMaps.m_Settings[16] = Integer.parseInt(file.get("操作", "显示系统虚拟按键", "0").toString());
		myMaps.m_Settings[17] = Integer.parseInt(file.get("操作", "是否允许穿越", "0").toString());
		myMaps.m_Settings[29] = Integer.parseInt(file.get("操作", "自动爬阶梯", "0").toString());
		myMaps.m_Settings[30] = Integer.parseInt(file.get("操作", "导出答案的注释信息", "1").toString());
		myMaps.m_Settings[31] = Integer.parseInt(file.get("操作", "自动打开导入关卡", "1").toString());
		myMaps.m_Settings[32] = Integer.parseInt(file.get("操作", "禁用逆推目标点", "0").toString());
		myMaps.m_Settings[38] = Integer.parseInt(file.get("操作", "区分奇偶地板格", "0").toString());
		myMaps.m_Settings[39] = Integer.parseInt(file.get("操作", "偶格位明暗度", "0").toString());
		myMaps.m_Settings[40] = Integer.parseInt(file.get("操作", "奇格位明暗度", "5").toString());
		myMaps.m_Settings[41] = Integer.parseInt(file.get("操作", "双击箱子编号", "0").toString());

		myMaps.m_Settings[21] = Integer.parseInt(file.get("编辑", "关卡编辑中，图中标尺的字体颜色", "1677721600").toString());
		myMaps.m_Settings[22] = Integer.parseInt(file.get("编辑", "关卡编辑中，携带标尺的元素", "1").toString());
		myMaps.m_Settings[19] = Integer.parseInt(file.get("编辑", "是否采用YASC绘制习惯", "0").toString());
		myMaps.m_Settings[36] = Integer.parseInt(file.get("识别", "图像识别", "0").toString());

		myMaps.nickname = file.get("提交", "nickname", "").toString();
		myMaps.uil = file.get("提交", "uil", "https://sokoban.cn/").toString();
		myMaps.country = file.get("提交", "country", "00").toString();
		myMaps.email = file.get("提交", "email", "").toString();
		myMaps.mMatchNo = file.get("比赛", "matchname", "").toString();
		myMaps.mMatchDate1 = file.get("比赛", "mmatchdate1", "").toString();
		myMaps.mMatchDate2 = file.get("比赛", "mmatchdate2", "").toString();

		myMaps.myPathList[2] = file.get("识别", "sPicPath", "").toString();
		myMaps.myPathList[3] = file.get("识别", "sPicPath2", "").toString();
		myMaps.myPathList[4] = file.get("识别", "sPicPath3", "").toString();

		if (myMaps.m_Settings[36] < 0 || myMaps.m_Settings[36] > 4) {
			myMaps.m_Settings[36] = 0;
		}
		if (myMaps.m_Settings[39] < 0) {
			myMaps.m_Settings[39] = 0;
		} else if (myMaps.m_Settings[39] > 15) {
			myMaps.m_Settings[39] = 15;
		}
		if (myMaps.m_Settings[40] < 0) {
			myMaps.m_Settings[40] = 5;
		} else if (myMaps.m_Settings[40] > 15) {
			myMaps.m_Settings[40] = 15;
		}
	}

	//保存快手设置
	static void saveSets() {
		IniFile file = new IniFile();

		file.set("常规", "当前关卡集组别", myMaps.m_Settings[0]);
		file.set("常规", "当前关卡集", myMaps.m_Settings[1]);
		file.set("常规", "预览时是否显示关卡标题", myMaps.m_Settings[2]);
		file.set("常规", "是否标识出重复关卡", myMaps.m_Settings[12]);
		file.set("常规", "浏览时每行的图标数", myMaps.m_Settings[33]);
		file.set("常规", "浏览时每行的图标默认数", myMaps.m_Settings[34]);
		file.set("常规", "语言设置", myMaps.localCode);				// Language: ZH = Chinese, EN = English

		file.set("界面", "背景色", myMaps.m_Settings[4]);
		file.set("界面", "皮肤", myMaps.skin_File);
		file.set("界面", "背景图片", myMaps.bk_Pic);
		file.set("界面", "背景时间", myMaps.m_Settings[25]);

		file.set("速度", "瞬移状态", myMaps.m_Settings[6]);
		file.set("速度", "移动速度", myMaps.m_Settings[10]);

		file.set("操作", "是否提示死锁", myMaps.m_Settings[11]);
		file.set("操作", "长按点位提示关联网", myMaps.m_Settings[3]);
		file.set("操作", "显示可达提示", myMaps.m_Settings[8]);
		file.set("操作", "仓管员转向动画", myMaps.m_Settings[27]);
		file.set("操作", "禁用全屏", myMaps.m_Settings[20]);
		file.set("操作", "演示时仅推动", myMaps.m_Settings[28]);
		file.set("操作", "标尺是否同步旋转", myMaps.m_Settings[9]);
		file.set("操作", "是否允许音量键选择关卡", myMaps.m_Settings[15]);
		file.set("操作", "显示系统虚拟按键", myMaps.m_Settings[16]);
		file.set("操作", "是否允许穿越", myMaps.m_Settings[17]);
		file.set("操作", "自动爬阶梯", myMaps.m_Settings[29]);
		file.set("操作", "导出答案的注释信息", myMaps.m_Settings[30]);
		file.set("操作", "自动打开导入关卡", myMaps.m_Settings[31]);
		file.set("操作", "禁用逆推目标点", myMaps.m_Settings[32]);
		file.set("操作", "区分奇偶地板格", myMaps.m_Settings[38]);
		file.set("操作", "偶格位明暗度", myMaps.m_Settings[39]);
		file.set("操作", "奇格位明暗度", myMaps.m_Settings[40]);
		file.set("操作", "双击箱子编号", myMaps.m_Settings[41]);

		file.set("编辑", "关卡编辑中，图中标尺的字体颜色", myMaps.m_Settings[21]);
		file.set("编辑", "关卡编辑中，携带标尺的元素", myMaps.m_Settings[22]);
		file.set("编辑", "是否采用YASC绘制习惯", myMaps.m_Settings[19]);
		file.set("识别", "图像识别", myMaps.m_Settings[36]);

		file.set("提交", "nickname", myMaps.nickname);
		file.set("提交", "uil", myMaps.uil);
		file.set("提交", "country", myMaps.country);
		file.set("提交", "email", myMaps.email);
		file.set("比赛", "matchname", myMaps.mMatchNo);
		file.set("比赛", "mmatchdate1", myMaps.mMatchDate1);
		file.set("比赛", "mmatchdate2", myMaps.mMatchDate2);

		file.set("识别", "sPicPath", myMaps.myPathList[2]);
		file.set("识别", "sPicPath2", myMaps.myPathList[3]);
		file.set("识别", "sPicPath3", myMaps.myPathList[4]);

		file.set("识别", "sPicPath3", myMaps.myPathList[4]);

		file.save(new File(myMaps.sRoot+myMaps.sPath + "BoxMan.ini"));
	}

	//为ExpandableListView自定义适配器
	class MyExpandableListView extends BaseExpandableListAdapter {

		//返回一级列表的个数
		@Override
		public int getGroupCount() {
			return groups.length;
		}

		//返回各二级列表的个数
		@Override
		public int getChildrenCount(int groupPosition) {
			int len = 0;
			switch (groupPosition){
				case 0:
					len = myMaps.mSets0.size();
					break;
				case 1:
					len = myMaps.mSets1.size();
					break;
				case 2:
					len = myMaps.mSets2.size();
					break;
				case 3:
					len = myMaps.mSets3.size();
			}
			return len;
		}

		//返回一级列表的单个item（返回的是对象）
		@Override
		public Object getGroup(int groupPosition) {
			return groups[groupPosition];
		}

		//返回二级列表中的单个item（返回的是对象）
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			Object o = null;
			switch (groupPosition){
				case 0:
					o = myMaps.mSets0.get(childPosition);
					break;
				case 1:
					o = myMaps.mSets1.get(childPosition);
					break;
				case 2:
					o = myMaps.mSets2.get(childPosition);
					break;
				case 3:
					o = myMaps.mSets3.get(childPosition);
			}

			return o;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		//每个item的id是否是固定，一般为true
		@Override
		public boolean hasStableIds() {
			return true;
		}

		//【重要】填充一级列表
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.v_groups, null);
			} else {

			}
			convertView.setTag(R.id.tag_g, groupPosition);
			convertView.setTag(R.id.tag_c, -1); //设置-1表示长按时点击的是父项，到时好判断。
			TextView tv_group = (TextView) convertView.findViewById(R.id.expGroup);

			String s = "";
			switch (groupPosition){
				case 0:
					s = " 【"+ myMaps.mSets0.size() + "】";
					break;
				case 1:
					s = " 【"+ myMaps.mSets1.size() + "】";
					break;
				case 2:
					s = " 【"+ myMaps.mSets2.size() + "】";
					break;
				case 3:
					s = " 【"+ myMaps.mSets3.size() + "】";
			}
			tv_group.setText(groups[groupPosition] + s);

			return convertView;
		}

		//【重要】填充二级列表
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.v_child, null);
			}

			convertView.setTag(R.id.tag_g, groupPosition);
			convertView.setTag(R.id.tag_c, childPosition);
			TextView tv_child = (TextView) convertView.findViewById(R.id.expChild);

			switch (groupPosition){
				case 0:
					tv_child.setText(myMaps.mSets0.get(childPosition).title + " （" + mySQLite.m_SQL.count_Sovled(myMaps.mSets0.get(childPosition).id) + "/" + mySQLite.m_SQL.count_Level(myMaps.mSets0.get(childPosition).id) + "）");
					break;
				case 1:
					tv_child.setText(myMaps.mSets1.get(childPosition).title + " （" + mySQLite.m_SQL.count_Sovled(myMaps.mSets1.get(childPosition).id) + "/" + mySQLite.m_SQL.count_Level(myMaps.mSets1.get(childPosition).id) + "）");
					break;
				case 2:
					tv_child.setText(myMaps.mSets2.get(childPosition).title + " （" + mySQLite.m_SQL.count_Sovled(myMaps.mSets2.get(childPosition).id) + "/" + mySQLite.m_SQL.count_Level(myMaps.mSets2.get(childPosition).id) + "）");
					break;
				case 3:
					tv_child.setText(myMaps.mSets3.get(childPosition).title + " （" + mySQLite.m_SQL.count_Sovled(myMaps.mSets3.get(childPosition).id) + "/" + mySQLite.m_SQL.count_Level(myMaps.mSets3.get(childPosition).id) + "）");
			}

			return convertView;
		}

		//二级列表中的item是否能够被选中
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	//从DB中加载关卡
	private void loadLevels(int groupPosition, int childPosition){

		long m_id = -1;
		switch (groupPosition){
			case 0:
				m_id = myMaps.mSets0.get(childPosition).id;
				myMaps.sFile = myMaps.mSets0.get(childPosition).title;
				break;
			case 1:
				m_id = myMaps.mSets1.get(childPosition).id;
				myMaps.sFile = myMaps.mSets1.get(childPosition).title;
				break;
			case 2:
				m_id = myMaps.mSets2.get(childPosition).id;
				myMaps.sFile = myMaps.mSets2.get(childPosition).title;
				break;
			case 3:
				m_id = myMaps.mSets3.get(childPosition).id;
				myMaps.sFile = myMaps.mSets3.get(childPosition).title;
		}
		mySQLite.m_SQL.get_Set(m_id);

//		exitTime = System.currentTimeMillis();
		mySQLite.m_SQL.get_Levels(m_id);
//		MyToast.showToast(this, ""+(System.currentTimeMillis() - exitTime), Toast.LENGTH_LONG);
	}

	//选择关卡集（列表）准备导入
	private void sel_Set(){

		myMaps.newSetList();

		if (myMaps.mFile_List.size() > 0) {
			View view = View.inflate(this, R.layout.import_dialog3, null);
			final CheckBox m_All = (CheckBox) view.findViewById(R.id.im_all);                       //全选
			myMaps.m_setName = (ListView) view.findViewById(R.id.im_sets);                          //关卡集文档列表
			final CheckBox m_XSB = (CheckBox) view.findViewById(R.id.im_xsb);                       //关卡
			final CheckBox m_LURD = (CheckBox) view.findViewById(R.id.im_lurd);                     //答案
            final RadioGroup myCodeGroup = (RadioGroup) view.findViewById(R.id.myCodeGroup3);  //文档编码格式
            final RadioButton rb_Code_GBK = (RadioButton) view.findViewById(R.id.rb_code_GBK3);  //GBK
            final RadioButton rb_Code_UTF8 = (RadioButton) view.findViewById(R.id.rb_code_utf83);  //UTF-8

			mySets = new ArrayList<Long>();
			for (int k = 0; k < myMaps.mFile_List.size(); k++) {
				mySets.add(( long )-(k+1));
			}
			mAdapter = new myMaps.MyAdapter(this, myMaps.mFile_List);
			myMaps.m_setName.setAdapter(mAdapter);                                                  //关卡集
			myMaps.m_setName.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);                          //开启多选模式
			for (int k = 0; k < myMaps.m_setName.getCount(); k++) {
				myMaps.m_setName.setItemChecked(k, false);
			}
			myMaps.m_setName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					mySets.set(position, -mySets.get(position));

					if (mySets.get(position) > 0) myMaps.m_setName.setItemChecked(position, true);
					else myMaps.m_setName.setItemChecked(position, false);

					//点击item后，通知adapter重新加载view
					mAdapter.notifyDataSetChanged();
				}
			});

			m_All.setChecked(false);                                                                //是否全选
			m_All.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					for (int k = 0; k < myMaps.m_setName.getCount(); k++) {
						myMaps.m_setName.setItemChecked(k, isChecked);
						if (isChecked) {
							if (mySets.get(k) < 0) mySets.set(k, -mySets.get(k));
						} else {
							if (mySets.get(k) > 0) mySets.set(k, -mySets.get(k));
						}
					}
				}
			});

			m_XSB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					myMaps.isXSB = isChecked;
					if (!isChecked && !m_LURD.isChecked())
						m_LURD.setChecked(true);
				}
			});
			m_LURD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					myMaps.isLurd = isChecked;
					if (!isChecked && !m_XSB.isChecked()) {
						m_XSB.setChecked(true);
					}
				}
			});
             myCodeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (rb_Code_UTF8.getId() == checkedId) {
                        myMaps.m_Code = 2;
                    } else if (rb_Code_GBK.getId() == checkedId) {
						myMaps.m_Code = 1;
                    } else {
                        myMaps.m_Code = 0;
                    }
                }
            });
            myMaps.m_Code = 0;
			andOpen = false;
			m_XSB.setChecked(true);
			AlertDialog.Builder dlg = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
			dlg.setView(view).setCancelable(false);
			dlg.setTitle(R.string.import_).setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.okay), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					imPort_Sets(myMaps.mFile_List, 2);  //导入关卡集
					expAdapter.notifyDataSetChanged();
					dialog.dismiss();
				}
			}).create().show();

		} else
			MyToast.showToast(this, getString(R.string.puzzle_set_file_not_found), Toast.LENGTH_SHORT);
	}

	//选择关卡集（列表）准备导出
	private void sel_Set2(){
		myMaps.isLurd = false;
		View view = View.inflate(this, R.layout.export_dialog3, null);
		final CheckBox m_All = (CheckBox) view.findViewById(R.id.ex_all);                       //全选
		final CheckBox m_Ans = (CheckBox) view.findViewById(R.id.ex_ans);                       //是否导出仅有答案的关卡
		myMaps.m_setName = (ListView) view.findViewById(R.id.ex_sets);                          //关卡集文档类别
		final CheckBox m_Comment = (CheckBox) view.findViewById(R.id.ex_comment3);              //答案含备注
		final CheckBox m_LURD = (CheckBox) view.findViewById(R.id.ex_lurd3);                    //答案
		final CheckBox m_ReWrite = (CheckBox) view.findViewById(R.id.ex_rewrite3);              //是否覆盖同名文档

		mySets = new ArrayList<Long>();                                                         //关卡集 id
		mAdapter = new myMaps.MyAdapter(this, (ArrayList)myMaps.getData(mySets));
		myMaps.m_setName.setAdapter(mAdapter);                                                  //关卡集
		myMaps.m_setName.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);                          //开启多选模式
		for (int k = 0; k < myMaps.m_setName.getCount(); k++) {
			myMaps.m_setName.setItemChecked(k, true);
		}
		myMaps.m_setName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mySets.set(position, -mySets.get(position));

				if (mySets.get(position) > 0) myMaps.m_setName.setItemChecked(position, true);
				else myMaps.m_setName.setItemChecked(position, false);

				//点击item后，通知adapter重新加载view
				mAdapter.notifyDataSetChanged();
			}
		});

		m_ReWrite.setChecked(true);  //遇到重复文档是否覆盖

		m_Ans.setChecked(true);  //是否导出仅有答案的关卡
		m_Ans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				myMaps.isXSB = isChecked;
			}
		});
		m_All.setChecked(true);                                                               //是否全选
		m_All.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (int k = 0; k < myMaps.m_setName.getCount(); k++) {
					myMaps.m_setName.setItemChecked(k, isChecked);
					if (isChecked) {
						if (mySets.get(k) < 0) mySets.set(k, -mySets.get(k));
					} else {
						if (mySets.get(k) > 0) mySets.set(k, -mySets.get(k));
					}
				}
			}
		});
		//是否导出答案
		m_LURD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				myMaps.isLurd = isChecked;
//				andAns = isChecked;
				m_Comment.setChecked(isChecked);
			}
		});
		//答案是否含备注
		m_Comment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) m_LURD.setChecked(isChecked);
				myMaps.isComment = isChecked;  //是否导出答案的备注信息
			}
		});
		m_LURD.setChecked(false);

		AlertDialog.Builder dlg = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
		dlg.setView(view).setCancelable(false);
		dlg.setTitle(getString(R.string.export)).setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.okay), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				long[] sets = new long[mySets.size()];
				for (int k = 0; k < sets.length; k++) {
					sets[k] = mySets.get(k);  //关卡集id列表
				}
				dialog.dismiss();
				exPort_Sets(sets, myMaps.isXSB, myMaps.isLurd, m_ReWrite.isChecked());
			}
		}).create().show();
	}

	//异步导入
	private void imPort_Sets(ArrayList<String> filelist, int act){
		if (mDialog == null) {
			mDialog = new mySplitLevelsFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("my_Type", act);                    //导入类别: 0 -- 剪切板关卡； 1 -- 文档关卡；2 -- 关卡集文档列表
			bundle.putStringArrayList("my_Files", filelist);  //关卡集列表
			mDialog.setArguments(bundle);
			mDialog.show(getFragmentManager(), TAG_PROGRESS_DIALOG_FRAGMENT);
		}
	}

	//异步导出
	private void exPort_Sets(long[] m_Sets, boolean andAns, boolean isLurd, boolean isReWrite){
		if (mDialog3 == null) {
			mDialog3 = new myExportFragment();
			Bundle bundle = new Bundle();
			bundle.putBoolean("my_Ans", andAns);              //是否导出仅答案关卡
			bundle.putBoolean("my_Lurd", isLurd);             //是否导出答案
			bundle.putBoolean("my_ReWrite", isReWrite);       //遇到重复文档是否覆盖
			bundle.putLongArray("my_SetIDs", m_Sets);         //关卡集id列表
			mDialog3.setArguments(bundle);
			mDialog3.show(getFragmentManager(), TAG_PROGRESS_DIALOG_FRAGMENT);
		}
	}

	//选择文档关卡集
	private void sel_File(){

		myMaps.newSetList();

		if (myMaps.mFile_List.size() > 0) {
			m_nItemSelect = -1;
			View view = View.inflate(this, R.layout.import_dialog, null);
			final CheckBox m_XSB = (CheckBox) view.findViewById(R.id.cb_xsb);  //关卡
			final CheckBox m_LURD = (CheckBox) view.findViewById(R.id.cb_lurd);  //答案
			final RadioGroup myCodeGroup = (RadioGroup) view.findViewById(R.id.myCodeGroup);  //文档编码格式
			final RadioButton rb_Code_GBK = (RadioButton) view.findViewById(R.id.rb_code_GBK);  //GBK
			final RadioButton rb_Code_UTF8 = (RadioButton) view.findViewById(R.id.rb_code_utf8);  //UTF-8
			final CheckBox m_Open = (CheckBox) view.findViewById(R.id.cb_open3);  //导入仅一个关卡时，自动打开

			m_XSB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					myMaps.isXSB = isChecked;
					andOpen = isChecked;
					if (!isChecked && !m_LURD.isChecked())
						m_LURD.setChecked(true);
				}
			});
			m_LURD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					myMaps.isLurd = isChecked;
					if (!isChecked && !m_XSB.isChecked()) {
						m_XSB.setChecked(true);
					}
				}
			});
			myCodeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					if (rb_Code_UTF8.getId() == checkedId) {
						myMaps.m_Code = 2;
					} else if (rb_Code_GBK.getId() == checkedId) {
						myMaps.m_Code = 1;
					} else {
						myMaps.m_Code = 0;
					}
				}
			});
			m_Open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					myMaps.m_Settings[31] = isChecked ? 1 : 0;
				}
			});
			m_Open.setChecked (myMaps.m_Settings[31] == 1 ? true : false);
			myMaps.m_Code = 0;
			m_XSB.setChecked(true);
			AlertDialog.Builder dlg = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
			dlg.setView(view).setCancelable(false);
			dlg.setTitle(getString(R.string.import_from_file)).setSingleChoiceItems(myMaps.mFile_List.toArray(new String[myMaps.mFile_List.size()]), -1, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					m_nItemSelect = which;
				}
			}).setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.okay), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (m_nItemSelect >= 0) {
						String setName = myMaps.mFile_List.get(m_nItemSelect);  //选择的文档
						myMaps.mFile_List.clear();
						myMaps.mFile_List.add(setName);
						imPort_Sets(myMaps.mFile_List, 1);  //导入文档关卡
					}
					dialog.dismiss();
				}
			}).create().show();

		} else
			MyToast.showToast(this, getString(R.string.puzzle_set_file_not_found), Toast.LENGTH_SHORT);
	}

	//解析剪切板中的关卡 ==》 指定的关卡集
	private void read_Plate() {
		String str = myMaps.loadClipper();
		if (str.equals("")) {
			MyToast.showToast(this, getString(R.string.no_puzzle_data_found_in_clipboard), Toast.LENGTH_SHORT);
		} else {
			View view = View.inflate(this, R.layout.import_dialog2, null);
			final EditText et = (EditText) view.findViewById(R.id.im_plate);  //剪切板
			final CheckBox m_XSB = (CheckBox) view.findViewById(R.id.cb_xsb2);  //关卡
			final CheckBox m_LURD = (CheckBox) view.findViewById(R.id.cb_lurd2);  //答案
			final CheckBox m_Open = (CheckBox) view.findViewById(R.id.cb_open2);  //导入仅一个关卡时，自动打开
			et.setTypeface(Typeface.MONOSPACE);
			et.setText(str);
			m_XSB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					myMaps.isXSB = isChecked;
					andOpen = isChecked;
					if (!isChecked && !m_LURD.isChecked())
						m_LURD.setChecked(true);
				}
			});
			m_LURD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					myMaps.isLurd = isChecked;
					if (!isChecked && !m_XSB.isChecked()) {
						m_XSB.setChecked(true);
					}
				}
			});
			m_Open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					myMaps.m_Settings[31] = isChecked ? 1 : 0;
				}
			});

			m_Open.setChecked (myMaps.m_Settings[31] == 1 ? true : false);
			myMaps.isLurd = false;
			m_XSB.setChecked(true);
			AlertDialog.Builder dlg = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
			dlg.setView(view).setCancelable(false);
			dlg.setTitle(R.string.clipboard_import).setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.okay), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					myMaps.mFile_List.clear();
					myMaps.mFile_List.add(et.getText().toString());
					imPort_Sets(myMaps.mFile_List, 0);  //导入剪切板关卡
				}
			}).create().show();
		}
	}

	@Override
	public void onSplitDone(String inf) {

		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}

		expAdapter.notifyDataSetChanged();
		setTitle(getString(R.string.app_name) + " " + mySQLite.m_SQL.count_Level());

		//导入统计提示
		if (andOpen && myMaps.m_Settings[31] == 1 && myMaps.m_Nums[2] == 1) {  //导入后打开（长按关卡集的导入，仅导入了一个有效的关卡时）
			mySQLite.m_SQL.get_Set(myMaps.m_Set_id);
			mySQLite.m_SQL.get_Last_Level(myMaps.m_Set_id);  //取得刚刚添加的关卡到“关卡列表”（仅含一个关卡的列表）

			if (0 == myMaps.m_Nums[3]) {  //关卡有效时
				if (0 < myMaps.m_Nums[1]) MyToast.showToast(this, getString(R.string.duplicate_or_invalid_solutions_not_imported), Toast.LENGTH_SHORT);
				myMaps.iskinChange = false;
				myMaps.sFile = "关卡导入";
				myMaps.curMap = myMaps.m_lstMaps.get(0);
				Intent intent1 = new Intent();
				intent1.setClass(this, myGameView.class);
				startActivity(intent1);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
				builder.setTitle(getString(R.string.title_information)).setMessage(inf).setPositiveButton(getString(R.string.okay), null);
				builder.setCancelable(false).create().show();
			}
			andOpen = false;
		} else {  //常规的导入（一般为导入关卡集）
			if (1 == myMaps.m_Nums[2] && 0 == myMaps.m_Nums[3] && (0 == myMaps.m_Nums[0] || 0 < myMaps.m_Nums[0] && 0 == myMaps.m_Nums[1])) {  //成功导入一个有效关卡且答案也没有差错时，简单提示即可
				MyToast.showToast(this, getString(R.string.imported_successfully), Toast.LENGTH_SHORT);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder (this, AlertDialog.THEME_HOLO_DARK);
				builder.setTitle (getString(R.string.text_)).setMessage (inf).setPositiveButton (getString(R.string.confirm), null);
				builder.setCancelable (false).create ().show ();
			}
		}
	}

	@Override
	public void onExportDone(String inf) {

		if (mDialog3 != null) {
			mDialog3.dismiss();
			mDialog3 = null;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
		builder.setTitle(R.string.stored_in_export_folder).setMessage(inf).setPositiveButton(getString(R.string.okay), null);
		builder.setCancelable(false).create().show();
	}

	@Override
	public void onQueryDone(ArrayList<mapNode> mlMaps) {
		if (mlMaps != null && mlMaps.size() > 0) {
			myMaps.curMap = null;
			myMaps.m_lstMaps.clear();

			myMaps.m_Settings[0] = 0;
			myMaps.m_Settings[1] = 0;
			myMaps.m_Set_id = -1;
			myMaps.sFile = getString(R.string.puzzle_search);
			myMaps.m_lstMaps = mlMaps;
			setTitle(myMaps.sFile);

			//关卡查询
			Intent intent = new Intent();
			intent.setClass(BoxMan.this, myGridView.class);
			startActivity(intent);
		} else {
			MyToast.showToast(this, getString(R.string.not_found), Toast.LENGTH_SHORT);
		}

		if (mDialog2 != null) {
			mDialog2.dismiss();
			mDialog2 = null;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		menu.add(0, 1, 0, getString(R.string.open));
		menu.add(0, 2, 0, getString(R.string.export___));
		menu.add(0, 3, 0,  getString(R.string.clear_state_history));
        menu.add(0, 4, 0,  getString(R.string.delete_solutions));
		if (groupPos > 2) {  //扩展组
            menu.add(0, 5, 0, R.string.rename___);
            menu.add(0, 6, 0,  getString(R.string.delete));
			menu.add(0, 7, 0,  getString(R.string.add_puzzle_file));
			menu.add(0, 8, 0,  getString(R.string.add_puzzle_clipboard));
			menu.add(0, 9, 0,  getString(R.string.add_competition_puzzles_sokobanWS));
		}
		menu.add(0, 10, 0,  getString(R.string.details_));
		myMaps.m_Settings[0] = groupPos;
		myMaps.m_Settings[1] = childPos;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case 1:  //打开
				browLevels(groupPos, childPos);
				break;
			case 2:  //导出
				if (childPos > -1){
//					andAns = false;
					myMaps.isLurd = false;
					long m_id = -1;
					switch (groupPos){
						case 0:
							m_id = myMaps.mSets0.get(childPos).id;
							myMaps.sFile = myMaps.mSets0.get(childPos).title;
							break;
						case 1:
							m_id = myMaps.mSets1.get(childPos).id;
							myMaps.sFile = myMaps.mSets1.get(childPos).title;
							break;
						case 2:
							m_id = myMaps.mSets2.get(childPos).id;
							myMaps.sFile = myMaps.mSets2.get(childPos).title;
							break;
						case 3:
							m_id = myMaps.mSets3.get(childPos).id;
							myMaps.sFile = myMaps.mSets3.get(childPos).title;
					}

					View view = View.inflate(this, R.layout.export2_dialog, null);
					final CheckBox m_LURD = (CheckBox) view.findViewById(R.id.ex_lurd);  //答案
					final CheckBox m_Comment = (CheckBox) view.findViewById(R.id.ex_comment);  //答案备注
					final CheckBox m_ReWrite = (CheckBox) view.findViewById(R.id.ex_rewrite);               //遇到重复文档是否覆盖
					m_LURD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							myMaps.isLurd = isChecked;
							m_Comment.setChecked(isChecked);
						}
					});
					m_Comment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if (isChecked) m_LURD.setChecked(isChecked);
							else myMaps.isComment = isChecked;  //是否导出答案的备注信息
						}
					});

					m_ReWrite.setChecked(true);   //遇到重复文档是否覆盖
					m_LURD.setChecked(false);

					AlertDialog.Builder dlg = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
					dlg.setView(view).setCancelable(false);
					final long finalM_id = m_id;
					dlg.setTitle(getString(R.string.export)).setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.okay), new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							long[] sets = new long[1];
							sets[0] = finalM_id;  //关卡集id列表
							dialog.dismiss();
							exPort_Sets(sets, false, myMaps.isLurd, m_ReWrite.isChecked());
						}
					}).create().show();
				}
				break;
			case 3:  //清理集内关卡状态
				new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK).setTitle(R.string.status_clean_up).setCancelable(false)
						.setMessage(R.string.delete_all_states_are_you_sure)
						.setPositiveButton(getString(R.string.okay), new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								long m_id = -1;
								switch (groupPos){
									case 0:
										m_id = myMaps.mSets0.get(childPos).id;
										break;
									case 1:
										m_id = myMaps.mSets1.get(childPos).id;
										break;
									case 2:
										m_id = myMaps.mSets2.get(childPos).id;
										break;
									case 3:
										m_id = myMaps.mSets3.get(childPos).id;
								}
								mySQLite.m_SQL.clear_S(m_id);
								MyToast.showToast(BoxMan.this, getString(R.string.clean_up), Toast.LENGTH_SHORT);
							}})
						.setNegativeButton(getString(R.string.cancel), null)
						.show();
				break;
            case 4:  // 删除关卡集的答案
                new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                        .setTitle(R.string.alertTitle_attention)
                        .setMessage(R.string.all_solutions_are_deleted_are_you_sure)
                        .setCancelable(false).setNegativeButton(getString(R.string.cancel), null)
                        .setPositiveButton(getString(R.string.okay), new OnClickListener(){
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                dialog = new ProgressDialog(BoxMan.this);
                                dialog.setMessage(getString(R.string.solution_deleted));
                                dialog.setCancelable(true);
                                new Thread(new delete2Thread()).start();
                                dialog.show();
                            }}).create().show();
                break;
			case 5:
				reName();   //重命名
				break;
			case 6:  // 删除关卡集
                new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                        .setTitle(R.string.alertTitle_attention).setMessage(getString(R.string.delete_puzzle_set_are_you_sure) + "\n（" + myMaps.mSets3.get(childPos).title+")")
                        .setCancelable(false).setNegativeButton(getString(R.string.cancel), null)
                        .setPositiveButton(getString(R.string.okay), new OnClickListener(){
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                dialog = new ProgressDialog(BoxMan.this);
                                dialog.setMessage(getString(R.string.deleting___));
                                dialog.setCancelable(true);
                                new Thread(new deleteThread()).start();
                                dialog.show();
                            }}).create().show();
                break;
            case 7:  //导入关卡（文档）
                myMaps.m_Set_id = myMaps.mSets3.get(childPos).id;
                sel_File();
                break;
            case 8:  //导入关卡（剪切板）
                myMaps.m_Set_id = myMaps.mSets3.get(childPos).id;
                read_Plate();
                break;
            case 9:  //导入比赛关卡
				View view = View.inflate(this, R.layout.get_uil_dialog, null);
				final EditText input_uil = (EditText) view.findViewById(R.id.dialog_uil);
				final EditText input_num = (EditText) view.findViewById(R.id.dialog_num2);
				input_uil.setText(myMaps.uil);
				input_num.setKeyListener(getNumber);
				// 新建一个可以添加属性的文本对象
				SpannableString ss = new SpannableString(getString(R.string.automatically_select_latest_competition));
				// 新建一个属性对象,设置文字的大小
				AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15,true);
				// 附加属性到文本
				ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				// 设置hint
				input_num.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失

				AlertDialog.Builder dlg = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
				dlg.setView(view).setCancelable(true);
				dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface di, int keyCode, KeyEvent event) {
						if(keyCode == KeyEvent.KEYCODE_ENTER){
							try {
								int n = Integer.parseInt(input_num.getText().toString());
								if (n > 0) {
									url_Num = "?id=" + String.valueOf(n);
								} else {
									url_Num = "";
								}
							} catch (Exception e) {
								url_Num = "";
							}

							di.dismiss();

							myMaps.uil = input_uil.getText().toString().trim();
							char ch = myMaps.uil.charAt(myMaps.uil.length()-1);
							if (ch != '/') {
								myMaps.uil = myMaps.uil + '/';
							}

							myMaps.m_Set_id = myMaps.mSets3.get(childPos).id;

							dialog = new ProgressDialog(BoxMan.this);
							dialog.setMessage(getString(R.string.downloading___));
							dialog.setCancelable(true);
							new Thread(new MyThread()).start();
							dialog.show();
							return true;
						}
						return false;
					}
				});
				dlg.setTitle(R.string.import_competition_puzzles).setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.okay), new OnClickListener() {
					@Override
					public void onClick(DialogInterface di, int which) {
						try {
							int n = Integer.parseInt(input_num.getText().toString());
							if (n > 0) {
								url_Num = "?id=" + String.valueOf(n);
							} else {
								url_Num = "";
							}
						} catch (Exception e) {
							url_Num = "";
						}

						di.dismiss();

						myMaps.uil = input_uil.getText().toString().trim();
						char ch = myMaps.uil.charAt(myMaps.uil.length()-1);
						if (ch != '/') {
							myMaps.uil = myMaps.uil + '/';
						}
						myMaps.m_Set_id = myMaps.mSets3.get(childPos).id;

						dialog = new ProgressDialog(BoxMan.this);
						dialog.setMessage(getString(R.string.downloading___));
						dialog.setCancelable(true);
						new Thread(new MyThread()).start();
						dialog.show();
					}
				}).setCancelable(false).create().show();

                break;
			case 10:  // 详细 == 关卡集的“关于...”
				long m_id = -1;
				String msg;
				switch (groupPos){
					case 0:
						m_id = myMaps.mSets0.get(childPos).id;
						myMaps.sFile = myMaps.mSets0.get(childPos).title;
						break;
					case 1:
						m_id = myMaps.mSets1.get(childPos).id;
						myMaps.sFile = myMaps.mSets1.get(childPos).title;
						break;
					case 2:
						m_id = myMaps.mSets2.get(childPos).id;
						myMaps.sFile = myMaps.mSets2.get(childPos).title;
						break;
					case 3:
						m_id = myMaps.mSets3.get(childPos).id;
						myMaps.sFile = myMaps.mSets3.get(childPos).title;
				}
				mySQLite.m_SQL.get_Set(m_id);
				msg = mySQLite.m_SQL.count_Sovled(m_id) + "/" + mySQLite.m_SQL.count_Level(m_id);
				//关卡集描述
				Intent intent1 = new Intent();

				//用Bundle携带数据
				Bundle bundle = new Bundle();
				bundle.putString("mMessage", msg);              //关卡数量
				intent1.putExtras(bundle);

				intent1.setClass(this, myAbout1.class);
				startActivity(intent1);
				break;
		}
		return true;
	}

	//比赛关卡下载完毕，关闭进度条
	private Handler handler = new Handler() {
		// 在Handler中获取消息，重写handleMessage()方法
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();

			String s;

			if (msg.what == 1) {
				expAdapter.notifyDataSetChanged();
				setTitle(getString(R.string.app_name) + " " + mySQLite.m_SQL.count_Level());
				s = getString(R.string.competition_information);
			} else {
				s = getString(R.string.failure);
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(BoxMan.this, AlertDialog.THEME_HOLO_DARK);
			builder.setTitle(s).setMessage(msg.obj.toString()).setPositiveButton(getString(R.string.okay), null);
			builder.setCancelable(false).create().show();
		}
	};

	//使用独立进程下载比赛关卡
	public class MyThread implements Runnable {
		@Override
		public void run() {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(myMaps.uil + url + url_Num);  // + "?id=112"
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				int code = httpResponse.getStatusLine().getStatusCode();
				if (code == 200) {
					String temp = EntityUtils.toString(httpResponse.getEntity());
					Message msg = Message.obtain();
					msg.what = 1;
					msg.obj = myJson(temp);  //解析并添加关卡
					handler.sendMessage(msg);
				} else {
					Message msg = Message.obtain();
					msg.what = 0;
					msg.obj = getString(R.string.network_error_) + code;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				Message msg = Message.obtain();
				msg.what = 0;
				msg.obj = getString(R.string.network_error);
				handler.sendMessage(msg);
			}
		}
	}

	//解析比赛关卡（使用 json-simple-1.1.jar，比较轻量级）
	private String myJson(String lvls) {
		String inf = getString(R.string.no_puzzle_data_found_or_game_not_started_yet);

		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject)parser.parse(lvls);

			// 解析string
			String m_no    = obj.get("id").toString();
			String m_begin = obj.get("begin").toString();
			String m_end   = obj.get("end").toString();

			myMaps.m_lstMaps.clear();  //关卡列表

			// 解析json中的关卡
			JSONObject level = (JSONObject)obj.get("main");
			if (level != null) {
				myMaps.m_lstMaps.add(new mapNode(level.get("level").toString(), level.get("title").toString(), level.get("author").toString(), ""));
			}

			level = (JSONObject)obj.get("extra");
			if (level != null) {
				myMaps.m_lstMaps.add(new mapNode(level.get("level").toString(), level.get("title").toString(), level.get("author").toString(), ""));
			}

			level = (JSONObject)obj.get("extra2");
			if (level != null) {
				myMaps.m_lstMaps.add(new mapNode(level.get("level").toString(), level.get("title").toString(), level.get("author").toString(), ""));
			}

			level = (JSONObject)obj.get("extra3");
			if (level != null) {
				myMaps.m_lstMaps.add(new mapNode(level.get("level").toString(), level.get("title").toString(), level.get("author").toString(), ""));
			}

			if (myMaps.m_lstMaps.size() > 0) {
				inf = "第" + m_no + "期比赛关卡加载成功！\n开始：" + m_begin + "\n结束：" + m_end;
				for (int k = 0; k < myMaps.m_lstMaps.size(); k++) {
					mySQLite.m_SQL.add_L(myMaps.m_Set_id, myMaps.m_lstMaps.get(k));   //添加的关卡库，P_id = myMaps.m_Set_id
				}
				myMaps.mMatchNo = "第" + m_no + "期比赛";
				myMaps.mMatchDate1 = m_begin;
				myMaps.mMatchDate2 = m_end;
			} else {
				inf = "第" + m_no + getString(R.string.not_yet_started) +"\n"+ getString(R.string.start_) + " " + m_begin + "\n" + getString(R.string.end_) + " " + m_end;
			}
		} catch (ParseException e) { }

		return inf;
	}

	//关卡集重命名
	private void reName() {
		myMaps.sFile = myMaps.mSets3.get(childPos).title;
		final EditText et = new EditText(this);
		et.setText(myMaps.sFile);
		et.setMaxLines(1);
		et.setSelection(et.getText().length());

		new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK).setTitle(getString(R.string.title_rename)).setCancelable(false)
				.setView(et).setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface di, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER){
					String input = et.getText().toString().trim();
					if (input.equals("")) {
						MyToast.showToast(BoxMan.this, getString(R.string.name_cannot_be_empty) + "\n" + input, Toast.LENGTH_SHORT);
					} else {
						if (mySQLite.m_SQL.find_Set(input, myMaps.mSets3.get(childPos).id) > 0) {
							MyToast.showToast(BoxMan.this, getString(R.string.this_name_already_exists)+"\n" + input, Toast.LENGTH_SHORT);
							et.setText(input);
							et.setSelection(input.length());
						} else {
							mySQLite.m_SQL.set_T_T(myMaps.mSets3.get(childPos).id, input);
							myMaps.mSets3.get(childPos).title = input;
							expAdapter.notifyDataSetChanged();
							di.dismiss();
							return true;
						}
					}
				}
				return false;
			}
		})
				.setPositiveButton(R.string.okay, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String input = et.getText().toString().trim();
						if (input.equals("")) {
							MyToast.showToast(BoxMan.this, getString(R.string.name_cannot_be_empty) + input, Toast.LENGTH_SHORT);
						} else {
							if (mySQLite.m_SQL.find_Set(input, myMaps.mSets3.get(childPos).id) > 0) {
								MyToast.showToast(BoxMan.this, getString(R.string.this_name_already_exists) + input, Toast.LENGTH_SHORT);
								et.setText(input);
								et.setSelection(input.length());
							} else {
								mySQLite.m_SQL.set_T_T(myMaps.mSets3.get(childPos).id, input);
								myMaps.mSets3.get(childPos).title = input;
								expAdapter.notifyDataSetChanged();
							}
						}
					}})
				.setNegativeButton(getString(R.string.cancel), null).show();
	}

	//删除关卡集完毕，关闭进度条
	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			expAdapter.notifyDataSetChanged();
			setTitle(getString(R.string.app_name) + " " + mySQLite.m_SQL.count_Level());
		}
	};

	//使用独立进程删除关卡集
	public class deleteThread implements Runnable {
		@Override
		public void run() {
			try {
				mySQLite.m_SQL.del_T(myMaps.mSets3.get(childPos).id);
				myMaps.mSets3.remove(childPos);

				Message msg = Message.obtain();
				msg.what = 1;
				handler2.sendMessage(msg);
			} catch (Exception e) {
				Message msg = Message.obtain();
				msg.what = 0;
				handler2.sendMessage(msg);
			}
		}
	}

	//删除关卡集完毕，关闭进度条
	private Handler handler3 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			expAdapter.notifyDataSetChanged();
			setTitle(getString(R.string.app_name) + " " + mySQLite.m_SQL.count_Level());
		}
	};

	//使用独立进程删除关卡集答案
	public class delete2Thread implements Runnable {
		@Override
		public void run() {
			try {
				if (groupPos == 0) {
					mySQLite.m_SQL.del_T_Ans(myMaps.mSets0.get(childPos).id);
				} else if (groupPos == 1) {
					mySQLite.m_SQL.del_T_Ans(myMaps.mSets1.get(childPos).id);
				} else if (groupPos == 2) {
					mySQLite.m_SQL.del_T_Ans(myMaps.mSets2.get(childPos).id);
				} else {
					mySQLite.m_SQL.del_T_Ans(myMaps.mSets3.get(childPos).id);
				}
				Message msg = Message.obtain();
				msg.what = 1;
				handler3.sendMessage(msg);
			} catch (Exception e) {
				Message msg = Message.obtain();
				msg.what = 0;
				handler3.sendMessage(msg);
			}
		}
	}

	//浏览关卡
	private void browLevels(int groupPos, int childPos){
		//确保关卡集进入一次 （点击太快，可能进入两次）
		if (myMaps.curJi) return;
		myMaps.curJi = true;

		//记住点击的位置，下次打开游戏时定位到此
		myMaps.m_Settings[0] = groupPos;
		myMaps.m_Settings[1] = childPos;

		//加载关卡
		loadLevels(groupPos, childPos);

		if (myMaps.m_lstMaps.size() < 1){
			MyToast.showToast(this, getString(R.string.toast_puzzle_not_found), Toast.LENGTH_SHORT);
			myMaps.curJi = false;
			return;
		}

		//启动关卡列表视图
		Intent intent1 = new Intent();
		intent1.setClass(this, myGridView.class);
		startActivity(intent1);
	}

	@Override
	protected void onStart() {
		if(myMaps.sRoot != null && expAdapter != null) {		// may be null if yet the user hasn't granted permissions
			setTitle(getString(R.string.app_name) + " " + mySQLite.m_SQL.count_Level());
			myMaps.curJi = false;
			expAdapter.notifyDataSetChanged();
			expView.expandGroup(myMaps.m_Settings[0]);
		}
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(expAdapter != null) expAdapter.notifyDataSetChanged();
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			return true;

		return super.onKeyUp(keyCode, event);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			exit();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	public void exit() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			MyToast.showToast(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT);
			exitTime = System.currentTimeMillis();
		} else {
			mySQLite.m_SQL.closeDataBase();
			saveSets();   //保存一些参数设置
			finish();
		}
	}

}