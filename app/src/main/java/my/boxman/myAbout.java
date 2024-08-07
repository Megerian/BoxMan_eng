package my.boxman;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class myAbout extends Activity {
	 private static TextView tv_help = null;

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.help);
		
        //开启标题栏的返回键
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

		tv_help = (TextView)this.findViewById(R.id.tvHelp);
		
		String s = "《推箱快手》\n\n" +
			   "  版本：9.99u~\n\n" +
			   "  作者：愉翁    QQ：252804303\n\n" +
			   "  策划：anian、愉翁\n\n" +
			   "  网站：sokoban.cn\n\n" +
			   "  QQ群：92017135\n\n" +
			   "  特别感谢：anian老师和杨超教授及众多热心箱友，anian老师在游戏设计的全过程中，" +
				"给出了大量的指导性意见，并进行了繁重的开发期测试，尤其在“割点”寻径及“穿越”寻径等算法方面，" +
				"更是得到了两位老师不遗余力的支持和帮助，许多算法都是直接移植于杨超教授的“SokoPlayer HTML5”。" +
				"另一方面，anian老师也为游戏提供了几乎全部的关卡集原始档案，还有少数关卡选自“http://sokoban.cn/”，" +
				"在游戏的公测期间，也收到了众多箱友的宝贵建议，这些建议包含了便捷操作、界面调整、功能增减以及错误修复等诸多方面。" +
				"可以说，离开了anian老师和杨超教授的倾心指导以及众位箱友热心支持，本游戏不会这么顺利地完成编写。" +
				"还有，从网上淘到“衣旧”网友编写的一个“ini文件工具类”，用在了系统配置的存取；" +
				"“闭口对角”死锁的检测代码和 XSB 的图像自动识别代码，移植于德国 Matthias Meger 大师的 JSoko；" +
				"以及 Kevin Weiner, FM Software 的 GIF 合成代码。" +
				"特此鸣谢！同时，也祝各位箱友都能很快的晋升到推箱子群中的快手之列！";

		if(myMaps.localCode.equalsIgnoreCase("en")) {
			 s = "《BoxMan》\n\n" +
					"  版Version：9.99u~\n\n" +
					"  Developer：Yú wēng 愉翁    QQ：252804303\n\n" +
					"  Design：anian、Yú wēng\n\n" +
					"  Website：sokoban.cn\n\n" +
					"  QQ Group：92017135\n\n" +

					"Special Thanks: A huge thank you to anian and Professor Yang Chao, " +
					"as well as the many enthusiastic Sokoban friends!\n\n" +

					"Throughout the game's design process, anian provided invaluable guidance and conducted " +
					"extensive testing during development.\n\n" +

					"We received exceptional support and assistance from both anian and Professor Yang, " +
					"particularly with algorithms like \"cut-point\" pathfinding and \"crossing\" pathfinding.\n\n" +

					"Many of these algorithms were directly ported from Professor Yang Chao's \"SokoPlayer HTML5.\"\n\n" +

					"Furthermore, anian provided almost all of the original level set files for the game.\n\n" +

					"A small number of levels were selected from \"http://sokoban.cn/\" .\n\n" +

					"During the game's public beta, we received valuable suggestions from many Sokoban players, " +
					"covering aspects such as ease of use, interface adjustments, feature additions and removals, and bug fixes.\n\n" +

					"It's safe to say that without the dedicated guidance of anian and Professor Yang Chao, " +
					"and the enthusiastic support of our fellow Sokoban players, this game would not have been completed so smoothly.\n\n" +

					"We would also like to acknowledge the following: An \"ini file utility class\" written by \"Yi Jiu\" found online, " +
					"which was used for storing and retrieving system configurations; \"Closed corner\" deadlock detection code " +
					"and XSB's automatic image recognition code, ported from JSoko by Matthias Meger from Germany;" +
					" and GIF synthesis code by Kevin Weiner, FM Software.\n\n" +

					"Thank you all! We also wish all Sokoban players a swift rise to the ranks of the fastest hands in the Sokoban community!";
		}

		tv_help.setText(s);
	 }
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
		 //标题栏返回键功能
		 case android.R.id.home:
//			Intent intent = new Intent(this, BoxMan.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
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
