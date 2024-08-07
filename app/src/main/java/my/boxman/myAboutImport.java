package my.boxman;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class myAboutImport extends Activity {
	 private static TextView tv_help = null;

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.help);
		
        //开启标题栏的返回键
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.title));

		tv_help = (TextView)this.findViewById(R.id.tvHelp);
		
		String s =
		 "一、“导入”规范：\n\n" +
		 "  1、仅正推支持“宏”功能；\n" +
		 "  2、逆推仅支持常规动作的导入，但是\n" +
		 "     常规动作前可包含用[x,y]的格式\n" +
		 "     指示的仓管员的开始坐标；\n" +
		 "  3、对常规动作（Lurd）进行编辑时，\n" +
		 "     可以进行旋转、镜像处理，还能进\n" +
		 "     行寄存（临时保存）等处理；\n" +
		 "  4、编辑的“宏”指令，不能寄存，但\n" +
		 "     可以文档形式存取，“宏”文档保\n" +
		 "     存在专门的“宏/”文件夹中；\n" +
		 "  5、对于正逆推的常规动作，可以选择\n" +
		 "     “从当前点”执行，若不勾选，则\n" +
		 "     表示从关卡的初始状态执行导入的\n" +
		 "     动作。勾选“按关卡之旋转”与菜\n" +
		 "     单中的旋转、翻转不是一个概念；\n" +
		 "  6、逆推动作前，可以不包含仓管员的\n" +
		 "     坐标，此时，一般是“从当前点”\n" +
		 "     执行导入的动作。正推动作前，可\n" +
		 "     包含坐标，此时，将按“宏”来处\n" +
		 "     理导入。\n\n" +
		 "二、关于逆推动作中坐标的特别说明：\n\n" +
		 "  1、逆推动作导入时，会涉及到两个坐\n" +
		 "     标：⑴是导入动作中的坐标，简称\n" +
		 "     “导入坐标”，⑵是关卡中仓管员\n" +
		 "     已有了坐标，简称“关卡坐标”；\n" +
		 "  2、执行前，若勾选“从当前点”，将\n" +
		 "     默认使用“关卡坐标”，否则，会\n" +
		 "     默认使用“导入坐标”；\n" +
		 "  3、当默认坐标不存在或无效时，会尝\n" +
		 "     试使用另一种坐标，即尽量执行导\n" +
		 "     入的动作。当两种坐标都不能使用\n" +
		 "     时，导入作废；\n" +
		 "  4、“导入坐标”无效指坐标在墙外或\n" +
		 "     坐标位置不能放置仓管员。“关卡\n" +
		 "     坐标”没有无效之说。";

		 if(myMaps.localCode.equalsIgnoreCase("en")) {
			 s =
				"Importing Move Sequences\n\n"+
			 "1. General Rules\n\n"+

			 "Forward Play: Supports importing macros (complex move sequences)." +

			 "Reverse Play: Supports importing regular moves (e.g., Left, Up, Right, Down).\n" +
			 "You can include the starting position of the pusher before the moves using the format [x,y].\n\n" +

			 "Editing Moves: You can rotate, mirror, and temporarily save regular moves while editing.\n\n" +

			 "Macros: Macros cannot be temporarily saved during editing. You can save and load macros as text files in the \"Macros\" folder.\n\n" +

			 "Start from Current Position: If selected, imported moves will be executed from the player's current position in the level.\n" +
		 	 "Otherwise, moves will be executed from the level's initial state.\n"+
			 "Note: \"Rotate with Level\" is different from the rotate/flip options in the menu." +
			 "Player coordinates: Reverse play moves don't require pusher coordinates.\n" +
		 	 "If no coordinates are provided, moves are usually executed from the current position.\n" +
			 "Forward play moves can include coordinates, in which case they will be treated as macros.\n\n" +

			 "2. Reverse Play and Coordinates\n\n " +

			 "Two Types of Coordinates: \n\n" +
			 "Imported Coordinates: The coordinates included in the imported move sequence.\n" +
			 "Level Coordinates: The pusher's current coordinates in the level.\n" +
			 "Coordinate Priority: If \"Start from Current Position\" is selected, level coordinates are used by default.\n" +
			 "Otherwise, imported coordinates are used.\n" +
			 "Invalid Coordinates: If the default coordinates are invalid (e.g., outside the level boundaries or on an obstacle), " +
			 "the app will try to use the other type of coordinates. If both are invalid, the import will be canceled.\n " +
			 "Invalid Imported Coordinates: Occur when the coordinates are outside the level or the pusher cannot be placed at that location.\n" +
			 "Puzzle coordinates are always considered valid.";
		 }

		tv_help.setText(s);
	 }
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
		 //标题栏返回键功能
		 case android.R.id.home:
			this.finish();
			return true;
		 default:
			return super.onOptionsItemSelected(item);
		 }
	}
		
	 @Override    
	 protected void onDestroy() { 
		 setContentView(R.layout.main);
		 super.onDestroy();
	 }

}
