package my.boxman;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class myActGMView extends Activity {

	 AlertDialog isSaveDlg;

	 EditText et_Action = null;
	 CheckBox et_curPos = null;
	 CheckBox et_curTrun = null;
	 CheckBox et_PreEdit = null;

	 Button btLoadAct, btSaveAct, btSave_t, btClear, btDO;

	 int m_nItemSelect;  //对话框中的出item选择前的记忆

	 Boolean is_BK;  //接收是否逆推界面
//	 String my_Local = "";  //接收关卡现场地图
	 private boolean flg = false;  //编辑框内容是否改动

	 private TextWatcher watcher = new TextWatcher(){
		@Override
		public void afterTextChanged(Editable arg0) {
			flg = true;
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
	 };

	@Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);

		 setContentView(R.layout.action_manage);

		//接收数据
		Bundle bundle = this.getIntent ().getExtras ();
		is_BK = bundle.getBoolean ("is_BK");  //接收是否逆推界面
//		my_Local = bundle.getString ("LOCAL");  //接收关卡现场地图

		 //开启标题栏的返回键
         ActionBar actionBar = getActionBar();
         actionBar.setDisplayHomeAsUpEnabled(true);
         actionBar.setDisplayShowHomeEnabled(false);
		 setTitle(getString(R.string.import2));

		 //界面初始化
		 et_Action = (EditText)this.findViewById(R.id.etAction);
		 btLoadAct = (Button) findViewById(R.id.btLoadAct);
		 btSaveAct = (Button) findViewById(R.id.btSavedAct);
		 btSave_t = (Button) findViewById(R.id.btSave_t);
		 btClear = (Button) findViewById(R.id.btClear);
		 btDO = (Button) findViewById(R.id.btDO);
		 et_curPos = (CheckBox) findViewById(R.id.cb_curPos);
		 et_curTrun = (CheckBox) findViewById(R.id.cb_curTrun);
		 et_PreEdit = (CheckBox) findViewById(R.id.cb_PreEdit);

		 et_Action.addTextChangedListener(watcher);

		 //动作编辑区
		et_PreEdit.setChecked(false);
		if (myMaps.isRecording) {  //录制模式且录制有效
			et_Action.setText(loadAct("act2"));
			saveAct("act2", "");  //录制模式仅仅借助该寄存器，所以，用后要清空
		}
		flg = false;  //编辑框内容是否改动
		myMaps.isRecording = false;  //关闭录制模式
		myMaps.isMacroDebug = false;  //单步宏

		//初次进入时，不显示输入法
		 getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		 AlertDialog.Builder builder = new Builder(this, AlertDialog.THEME_HOLO_DARK);
		 builder.setMessage(getString(R.string.save_temporarily_are_you_sure)).setCancelable(false)
			.setNegativeButton(getString(R.string.cancel), null)
			.setNeutralButton(getString(R.string.no), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					myActGMView.this.finish();
				}
			})
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveAct("reg0", et_Action.getText().toString());  //暂存当前编辑区内容
					myActGMView.this.finish();
				}
			});
		 isSaveDlg = builder.create();

		 //是否从关卡的当前点执行动作
         myMaps.m_ActionIsPos = et_curPos.isChecked();
		 et_curPos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 myMaps.m_ActionIsPos = isChecked;
             }
         });

		 //是否按关卡的当前旋转状态执行动作
         myMaps.m_ActionIsTrun = et_curTrun.isChecked();
		 et_curTrun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 myMaps.m_ActionIsTrun = isChecked;
             }
         });

		 //是否加载上次编辑的动作
		 et_PreEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				 if (isChecked) {  //加载上次编辑的动作
					 et_Action.setText(loadAct("reg0"));
				 } else {  //加载剪切板
					 et_Action.setText(myMaps.loadLURD(myMaps.loadClipper(), is_BK ? -1 : 1));
				 }
				 flg = false;  //编辑框内容是否改动
		 }});

		 //暂存按钮
		 btSave_t.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 if (et_Action.getText().toString().replaceAll("[ \n\r\t]", "").length() > 0) {
					 AlertDialog.Builder builder = new Builder(myActGMView.this, AlertDialog.THEME_HOLO_DARK);
					 builder.setMessage(getString(R.string.save_temporarily_are_you_sure)).setCancelable(false)
							 .setNegativeButton(getString(R.string.cancel), null)
							 .setPositiveButton(getString(R.string.positiveButton_confirm), new DialogInterface.OnClickListener(){
								 @Override
								 public void onClick(DialogInterface dialog, int which) {
									 saveAct("reg0", et_Action.getText().toString());  //暂存当前编辑区内容
									 MyToast.showToast(myActGMView.this, getString(R.string.successfully_saved), Toast.LENGTH_SHORT);
								 }
							 }).create().show();
				 } else {
					 MyToast.showToast(myActGMView.this, getString(R.string.there_is_nothing_to_save), Toast.LENGTH_SHORT);
				 }
		 }});

		 //加载按钮
		 btLoadAct.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 if (flg && et_Action.getText().toString().replaceAll("[ \n\r\t]", "").length() > 0) {  //求解前，若编辑过则提示保存
					 AlertDialog.Builder builder = new Builder(myActGMView.this, AlertDialog.THEME_HOLO_DARK);
					 builder.setMessage(getString(R.string.do_you_want_to_save_the_changes_temporarily)).setCancelable(false)
							 .setNegativeButton(getString(R.string.cancel), null)
							 .setNeutralButton(getString(R.string.no), new DialogInterface.OnClickListener(){
								 @Override
								 public void onClick(DialogInterface dialog, int which) {
									 myLoad();
								 }
							 })
							 .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
								 @Override
								 public void onClick(DialogInterface dialog, int which) {
									 saveAct("reg0", et_Action.getText().toString());  //暂存当前编辑区内容
									 myLoad();
								 }
							 }).create().show();
				 } else {
					 myLoad();
				 }
			 }
		 });

		 //寄存
		 btSaveAct.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 String[] m_menu = {
						 "宏",
						 "剪切板",
						 "寄存器1",
						 "寄存器2",
						 "寄存器3",
						 "寄存器4",
						 "寄存器5",
						 "寄存器6",
						 "寄存器7",
						 "寄存器8",
						 "寄存器9"
				 };
				 AlertDialog.Builder builder1 = new Builder(myActGMView.this, AlertDialog.THEME_HOLO_DARK);
				 builder1.setTitle("保存到").setSingleChoiceItems(m_menu, -1, new DialogInterface.OnClickListener() {
					 @Override
					 public void onClick(DialogInterface dialog, int which) {
						 switch (which) {
							 case 0:  //保存：宏
								 if (is_BK) {  //逆推
									 MyToast.showToast(myActGMView.this, getString(R.string.reverse_push_does_not_support_macro_functionality), Toast.LENGTH_SHORT);
								 } else {
									 saveMacroFile();
								 }
								 break;
							 case 1:  //送入：剪切板
								 myMaps.saveClipper(et_Action.getText().toString());
								 break;
							 case 2:  //下面为送入各寄存器
								 saveAct("reg1", et_Action.getText().toString());
								 break;
							 case 3:
								 saveAct("reg2", et_Action.getText().toString());
								 break;
							 case 4:
								 saveAct("reg3", et_Action.getText().toString());
								 break;
							 case 5:
								 saveAct("reg4", et_Action.getText().toString());
								 break;
							 case 6:
								 saveAct("reg5", et_Action.getText().toString());
								 break;
							 case 7:
								 saveAct("reg6", et_Action.getText().toString());
								 break;
							 case 8:
								 saveAct("reg7", et_Action.getText().toString());
								 break;
							 case 9:
								 saveAct("reg8", et_Action.getText().toString());
								 break;
							 case 10:
								 saveAct("reg9", et_Action.getText().toString());
								 break;
						 }
						 dialog.dismiss();
					 }
				 }).setPositiveButton(getString(R.string.cancel), null);
				 builder1.setCancelable(false).show();
			 }
		 });

		 //清空动作
		 btClear.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 et_Action.setText("");
				 flg = false;  //编辑框内容是否改动
			 }
		 });

		 //执行动作
		 btDO.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 // 判断name中是否包含字母
				 if (is_BK && !myMaps.isLURD2(et_Action.getText().toString())) {  //逆推
					 MyToast.showToast(myActGMView.this, getString(R.string.invalid_characters_for_reverse_mode), Toast.LENGTH_SHORT);
				 } else {
					 myMaps.m_ActionIsRedy = true;
					 // 对于非“宏”，自动加上忽略大小写命令
					 if (myMaps.isLURD(et_Action.getText().toString())) {
						 myMaps.sAction = new String[1];
						 myMaps.sAction[0] = "{" + et_Action.getText().toString() + "}~";
					 } else {
						 myMaps.sAction = et_Action.getText().toString().split("\n|\r|\n\r|\r\n|\\|");
					 }

					 //提前去掉注释行和两端的空格及制表符，方便以后处理
					 int len = myMaps.sAction.length;
					 int w, w1, w2;
					 String str;
					 for (int k = 0; k < len; k++) {
						 if (myMaps.sAction[k].trim().isEmpty()) {
							 myMaps.sAction[k] = "";
						 	continue;
						 } else {
							 str = myMaps.qj2bj(myMaps.sAction[k]).trim();  //全角转换成半角
						 }
						 w = str.indexOf('<');
						 if (w >= 0) {
							 w1 = str.indexOf(';');
							 if (w1 >= 0 && w1 < w) w = w1;  //若注释符号在行内块之前
							 else {
								 w2 = str.indexOf('>');
								 w = str.indexOf(';', w2);  //行内块后面的注释符号
							 }
						 } else {
						 	w = str.indexOf(';');
						 }

						 if (w > 0) {
							 myMaps.sAction[k] = str.substring(0, w).replaceAll("[\t]", " ").trim();
						 } else if (w == 0) {
							 myMaps.sAction[k] = "";
						 } else {
							 myMaps.sAction[k] = str;
						 }
					 }
					 if (flg && et_Action.getText().toString().replaceAll("[ \n\r\t]", "").length() > 0)
						 isSaveDlg.show();
					 else finish();
				 }
			 }
		 });
	 }

	private void myLoad() {
		String[] m_menu = {
				"宏",
				"已做动作",
				"后续动作",
				"文档",
				"剪切板",
				"寄存器1",
				"寄存器2",
				"寄存器3",
				"寄存器4",
				"寄存器5",
				"寄存器6",
				"寄存器7",
				"寄存器8",
				"寄存器9"
		};
		AlertDialog.Builder builder1 = new Builder(myActGMView.this, AlertDialog.THEME_HOLO_DARK);
		builder1.setTitle(getString(R.string.select_puzzle)).setSingleChoiceItems(m_menu, -1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:  //读取：宏
						if (is_BK) {  //逆推
							MyToast.showToast(myActGMView.this, getString(R.string.reverse_push_does_not_support_macro_functionality), Toast.LENGTH_SHORT);
						} else {
							m_nItemSelect = -1;
							myMaps.mMacroList();
							if (myMaps.mFile_List.size() > 0) {
								new Builder(myActGMView.this, AlertDialog.THEME_HOLO_DARK).setTitle(R.string.reading_macro).setCancelable(false)
										.setSingleChoiceItems(myMaps.mFile_List.toArray(new String[myMaps.mFile_List.size()]), -1, new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												m_nItemSelect = which;
											}
										}).setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										if (m_nItemSelect > -1) {
											et_PreEdit.setChecked(false);
											et_Action.setText(myMaps.readMacroFile(myMaps.mFile_List.get(m_nItemSelect)));  //选择的文档
											flg = false;
										}
									}
								}).show();
							} else {
								MyToast.showToast(myActGMView.this, getString(R.string.please_copy_the_macro_file_to_the_macros_folder), Toast.LENGTH_SHORT);
							}
						}
						break;
					case 1:  //读取：已做动作
						et_PreEdit.setChecked(false);
						et_Action.setText(loadAct("act1"));
						flg = false;
						break;
					case 2:  //读取：后续动作
						et_PreEdit.setChecked(false);
						et_Action.setText(loadAct("act2"));
						flg = false;
						break;
					case 3:  //读取：文档
						m_nItemSelect = -1;
						myMaps.newSetList();
						if (myMaps.mFile_List.size() > 0) {
							new Builder(myActGMView.this, AlertDialog.THEME_HOLO_DARK).setTitle("文档：导入").setCancelable(false)
									.setSingleChoiceItems(myMaps.mFile_List.toArray(new String[myMaps.mFile_List.size()]), -1, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											m_nItemSelect = which;
										}
									}).setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									if (m_nItemSelect > -1) {
										et_PreEdit.setChecked(false);
										et_Action.setText(readFile(myMaps.mFile_List.get(m_nItemSelect)));  //选择的文档
										flg = false;
									}
								}
							}).show();
						} else {
							MyToast.showToast(myActGMView.this, getString(R.string.please_copy_the_file_to_the_import_folder), Toast.LENGTH_SHORT);
						}
						break;
					case 4:  //读取：剪切板
						et_PreEdit.setChecked(false);
						et_Action.setText(myMaps.loadClipper());
						flg = false;
						break;
					case 5:  //下面为读取各寄存器
						et_PreEdit.setChecked(false);
						et_Action.setText(loadAct("reg1"));
						flg = false;
						break;
					case 6:
						et_PreEdit.setChecked(false);
						et_Action.setText(loadAct("reg2"));
						flg = false;
						break;
					case 7:
						et_PreEdit.setChecked(false);
						et_Action.setText(loadAct("reg3"));
						flg = false;
						break;
					case 8:
						et_PreEdit.setChecked(false);
						et_Action.setText(loadAct("reg4"));
						flg = false;
						break;
					case 9:
						et_PreEdit.setChecked(false);
						et_Action.setText(loadAct("reg5"));
						flg = false;
						break;
					case 10:
						et_PreEdit.setChecked(false);
						et_Action.setText(loadAct("reg6"));
						flg = false;
						break;
					case 11:
						et_PreEdit.setChecked(false);
						et_Action.setText(loadAct("reg7"));
						flg = false;
						break;
					case 12:
						et_PreEdit.setChecked(false);
						et_Action.setText(loadAct("reg8"));
						flg = false;
						break;
					case 13:
						et_PreEdit.setChecked(false);
						et_Action.setText(loadAct("reg9"));
						flg = false;
						break;
				}
				dialog.dismiss();
			}
		}).setPositiveButton(getString(R.string.cancel), null);
		builder1.setCancelable(false).show();
	}
	 //读入寄存器
	 private String loadAct(String name) {
		try {
			SharedPreferences sp = getSharedPreferences("BoxMan", Context.MODE_PRIVATE);
			return sp.getString(name, "");
		} catch (Exception e){
			return "";
		}
	 }

	 //保存
	 private void saveAct(String name, String value) {
		 if (myMaps.isRecording && value.isEmpty()) {  // 录制模式下的清理动作
		 } else if (!name.equals("reg0") && !myMaps.isLURD(value)) {
			 MyToast.showToast(this, getString(R.string.encountered_invalid_characters_save_failed), Toast.LENGTH_SHORT);
			 return;
		 }
		try {
			SharedPreferences.Editor editor = getSharedPreferences("BoxMan", Context.MODE_PRIVATE).edit();
			editor.putString(name, value);
			editor.commit();
			if (myMaps.isRecording && value.isEmpty()) {  // 录制模式下的清理动作
			} else {
				MyToast.showToast(myActGMView.this, getString(R.string.saved), Toast.LENGTH_SHORT);
			}
			flg = false;  //编辑框内容是否改动
		} catch (Exception e){ }
	 }

	//从文档读入 Lurd
	private String readFile(String fn) {
		InputStream fin;
		String my_Name;
		try {
			my_Name = new StringBuilder(myMaps.sRoot).append(myMaps.sPath).append("导入/").append(fn).toString();
			fin = new FileInputStream(my_Name);

			int len = fin.available();
			byte[] Buf = new byte[len];
			fin.read(Buf);
			fin.close();

			return myMaps.loadLURD(new String(Buf), 0);
		} catch (Exception e) {
			MyToast.showToast(this, getString(R.string.invalid_data_in_the_file), Toast.LENGTH_SHORT);
		}
		return "";
	}

	//保存“宏”到文档
	private void saveMacroFile() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");//设置日期格式
		final String fn = "M_" + df.format(new Date()) + ".txt";  // new Date()获取当前系统时间

		final EditText et = new EditText(this);
		et.setBackgroundColor(0xff444444);
		et.setText(fn);

		myMaps.mMacroList();
		myMaps.mFile_List.add(0, "自动名称");

		new Builder(this, AlertDialog.THEME_HOLO_DARK).setTitle(R.string.macro_name).setView(et)
				.setSingleChoiceItems(myMaps.mFile_List.toArray(new String[myMaps.mFile_List.size()]), -1, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which > 0) {
							et.setText(myMaps.mFile_List.get(which));
						} else {
							et.setText(fn);
						}
					}
				}).setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					File targetDir = new File(myMaps.sRoot + myMaps.sPath + "宏/");
					myMaps.mFile_List.clear();
					if (!targetDir.exists()) targetDir.mkdirs();  //创建"宏/"文件夹

					String str = et.getText().toString().trim();
					String prefix = str.substring(str.lastIndexOf(".") + 1);
					if (!prefix.equalsIgnoreCase("txt")) {
						str = str + ".txt";
					}
					final String my_Name = new StringBuilder(myMaps.sRoot).append(myMaps.sPath).append("宏/").append(str).toString();

					File file = new File(my_Name);
					if (file.exists()) {
						new Builder(myActGMView.this, AlertDialog.THEME_HOLO_DARK).setMessage(getString(R.string.file_exists_overwrite) + str)
								.setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.overwrite), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								try {
									FileOutputStream fout = new FileOutputStream(my_Name);
									fout.write(et_Action.getText().toString().getBytes());
									fout.flush();
									fout.close();
                                    MyToast.showToast(myActGMView.this, getString(R.string.saved_successfully), Toast.LENGTH_SHORT);
								} catch (Exception e) {
									MyToast.showToast(myActGMView.this, getString(R.string.write_error_save_failed), Toast.LENGTH_SHORT);
								}
							}
						}).setCancelable(false).show();
					} else {
//                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(my_Name), "utf-8"));
//                        writer.write(et_Action.getText().toString());
//                        writer.close();

						FileOutputStream fout = new FileOutputStream(my_Name);
						fout.write(et_Action.getText().toString().getBytes());
						fout.flush();
						fout.close();
                        MyToast.showToast(myActGMView.this, getString(R.string.saved_successfully), Toast.LENGTH_SHORT);
					}
				} catch (Exception e) {
					MyToast.showToast(myActGMView.this, getString(R.string.write_error_save_failed), Toast.LENGTH_SHORT);
				}
			}
		}).setNegativeButton(getString(R.string.cancel), null).setCancelable(false).show();
	}

	@Override
	protected void onResume() {
		if (!myMaps.isRecording && myMaps.m_MapChange) {  //非录制模式或录制被取消，且为首次进入该关卡的导入功能
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// 安卓10以上，需要延时一下才能获取剪切板内容
					et_Action.setText(myMaps.loadLURD(myMaps.loadClipper(), is_BK ? -1 : 1));  //加载“剪切板”
					myMaps.m_MapChange = false;  //是否切换了关卡
				}
			}, 500);// 500毫秒后执行Runnable中的run方法
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
//		setContentView(R.layout.game_view);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.act_gm, menu);

		//逆推时，无 YASS 求解功能
		if (is_BK) menu.getItem(0).setVisible(false);
		else menu.getItem(0).setVisible(true);

		return true;
	}

//	//接收 YASS 返回值
//	protected void onActivityResult(int requestCode, int resultCode, Intent data){
//		super.onActivityResult(requestCode, resultCode, data);
//
//		if (requestCode == 1) {  //求解返回
//			if (resultCode == RESULT_OK) {
//				et_PreEdit.setChecked(false);
//				et_Action.setText(data.getStringExtra("SOLUTION"));
//				et_curPos.setChecked(true);
//				flg = false;  //求解返回时，不算作编辑改变
//				MyToast.showToast(this, "求解成功！", Toast.LENGTH_SHORT);
//			} else {
//				MyToast.showToast(this, "未能完成求解！", Toast.LENGTH_SHORT);
//			}
//		}
//	}
//
//	//求解
//	protected void mySolution() {
//		try {
//			Intent intent3 = new Intent(Intent.ACTION_MAIN);
//			intent3.addCategory(Intent.CATEGORY_LAUNCHER);
//			ComponentName name = new ComponentName("net.sourceforge.sokobanyasc.joriswit.yass", "yass.YASSActivity");
//			intent3.setComponent(name);
//			String actName = intent3.getAction();
//			intent3.setAction("nl.joriswit.sokosolver.SOLVE");
//			intent3.putExtra("LEVEL", my_Local);
//			startActivityForResult(intent3, 1);
//			intent3.setAction(actName);
//		} catch (Exception e) {
//			MyToast.showToast(this, "没有找到求解器！", Toast.LENGTH_SHORT);
//		}
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem mt) {
		Pattern p = Pattern.compile("l|L|r|R|u|U|d|D");
		Matcher m;
		StringBuffer sb;

		switch (mt.getItemId()) {
			//菜单栏返回键功能
			case android.R.id.home:
				//内容有变化，提问是否保存
				if (flg && et_Action.getText().toString().replaceAll("[ \n\r\t]", "").length() > 0) isSaveDlg.show();
				else finish();
				return true;
			case R.id.act_about:  //“导入”说明
				Intent intent0 = new Intent(this, Help.class);
				//用Bundle携带数据
				Bundle bundle0 = new Bundle();
				bundle0.putInt("m_Num", 4);  //传递参数，指示调用者
				intent0.putExtras(bundle0);

				intent0.setClass(this, Help.class);
				startActivity(intent0);

				return true;
			case R.id.act_micro_about:  //“宏”功能说明
				Intent intent1 = new Intent(this, Help.class);
				//用Bundle携带数据
				Bundle bundle1 = new Bundle();
				bundle1.putInt("m_Num", 5);  //传递参数，指示调用者
				intent1.putExtras(bundle1);

				intent1.setClass(this, Help.class);
				startActivity(intent1);


				return true;
			case R.id.act_L_90:  //左旋90度（Lurd）
				if (myMaps.isLURD(et_Action.getText().toString())) {
					m = p.matcher(et_Action.getText());
					sb = new StringBuffer();
					while (m.find()) {
						if (m.group().equals("l")) m.appendReplacement(sb, "d");
						else if (m.group().equals("L")) m.appendReplacement(sb, "D");
						else if (m.group().equals("u")) m.appendReplacement(sb, "l");
						else if (m.group().equals("U")) m.appendReplacement(sb, "L");
						else if (m.group().equals("r")) m.appendReplacement(sb, "u");
						else if (m.group().equals("R")) m.appendReplacement(sb, "U");
						else if (m.group().equals("d")) m.appendReplacement(sb, "r");
						else if (m.group().equals("D")) m.appendReplacement(sb, "R");
					}
					m.appendTail(sb);
					et_Action.setText(sb.toString());
				} else {
					MyToast.showToast(this, "仅支持规范的动作字符！", Toast.LENGTH_SHORT);
				}
				return true;
			case R.id.act_R_90:  //右旋90度（Lurd）
				if (myMaps.isLURD(et_Action.getText().toString())) {
					m = p.matcher(et_Action.getText());
					sb = new StringBuffer();
					while (m.find()) {
						if(m.group().equals("l")) m.appendReplacement(sb, "u");
						else if(m.group().equals("L")) m.appendReplacement(sb, "U");
						else if(m.group().equals("u")) m.appendReplacement(sb, "r");
						else if(m.group().equals("U")) m.appendReplacement(sb, "R");
						else if(m.group().equals("r")) m.appendReplacement(sb, "d");
						else if(m.group().equals("R")) m.appendReplacement(sb, "D");
						else if(m.group().equals("d")) m.appendReplacement(sb, "l");
						else if(m.group().equals("D")) m.appendReplacement(sb, "L");
					}
					m.appendTail(sb);
					et_Action.setText(sb.toString());
				} else {
					MyToast.showToast(this, "仅支持规范的动作字符！", Toast.LENGTH_SHORT);
				}
				return true;
			case R.id.act_180:  //右旋180度（Lurd）
				if (myMaps.isLURD(et_Action.getText().toString())) {
					m = p.matcher(et_Action.getText());
					sb = new StringBuffer();
					while (m.find()) {
						if(m.group().equals("l")) m.appendReplacement(sb, "r");
						else if(m.group().equals("L")) m.appendReplacement(sb, "R");
						else if(m.group().equals("u")) m.appendReplacement(sb, "d");
						else if(m.group().equals("U")) m.appendReplacement(sb, "D");
						else if(m.group().equals("r")) m.appendReplacement(sb, "l");
						else if(m.group().equals("R")) m.appendReplacement(sb, "L");
						else if(m.group().equals("d")) m.appendReplacement(sb, "u");
						else if(m.group().equals("D")) m.appendReplacement(sb, "U");
					}
					m.appendTail(sb);
					et_Action.setText(sb.toString());
				} else {
					MyToast.showToast(this, "仅支持规范的动作字符！", Toast.LENGTH_SHORT);
				}
				return true;
			case R.id.act_LR:  //左右翻转（Lurd）
				if (myMaps.isLURD(et_Action.getText().toString())) {
					m = p.matcher(et_Action.getText());
					sb = new StringBuffer();
					while (m.find()) {
						if(m.group().equals("l")) m.appendReplacement(sb, "r");
						else if(m.group().equals("L")) m.appendReplacement(sb, "R");
						else if(m.group().equals("u")) m.appendReplacement(sb, "u");
						else if(m.group().equals("U")) m.appendReplacement(sb, "U");
						else if(m.group().equals("r")) m.appendReplacement(sb, "l");
						else if(m.group().equals("R")) m.appendReplacement(sb, "L");
						else if(m.group().equals("d")) m.appendReplacement(sb, "d");
						else if(m.group().equals("D")) m.appendReplacement(sb, "D");
					}
					m.appendTail(sb);
					et_Action.setText(sb.toString());
				} else {
					MyToast.showToast(this, "仅支持规范的动作字符！", Toast.LENGTH_SHORT);
				}
				return true;
			case R.id.act_UD:  //上下翻转（Lurd）
				if (myMaps.isLURD(et_Action.getText().toString())) {
					m = p.matcher(et_Action.getText());
					sb = new StringBuffer();
					while (m.find()) {
						if(m.group().equals("l")) m.appendReplacement(sb, "l");
						else if(m.group().equals("L")) m.appendReplacement(sb, "L");
						else if(m.group().equals("u")) m.appendReplacement(sb, "d");
						else if(m.group().equals("U")) m.appendReplacement(sb, "D");
						else if(m.group().equals("r")) m.appendReplacement(sb, "r");
						else if(m.group().equals("R")) m.appendReplacement(sb, "R");
						else if(m.group().equals("d")) m.appendReplacement(sb, "u");
						else if(m.group().equals("D")) m.appendReplacement(sb, "U");
					}
					m.appendTail(sb);
					et_Action.setText(sb.toString());
				} else {
					MyToast.showToast(this, "仅支持规范的动作字符！", Toast.LENGTH_SHORT);
				}
				return true;
			case R.id.act_recording:
				myMaps.isRecording = true;  //开启录制模式
				if (flg && et_Action.getText().toString().replaceAll("[ \n\r\t]", "").length() > 0) isSaveDlg.show();
				else finish();
				return true;
			default:
				return super.onOptionsItemSelected(mt);
		}
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
	 	if (keyCode == KeyEvent.KEYCODE_BACK)
	 		return true;
	 	
	 	return super.onKeyUp(keyCode, event);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
	 	if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (flg && et_Action.getText().toString().replaceAll("[ \n\r\t]", "").length() > 0) isSaveDlg.show();
			else finish();

	 		return true;
	 	}
	 	return super.onKeyDown(keyCode, event);
	}
}
