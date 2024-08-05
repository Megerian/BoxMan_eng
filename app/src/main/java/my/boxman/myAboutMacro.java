package my.boxman;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class myAboutMacro extends Activity {
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
		 "声明：仅正推支持“宏”功能！\n\n" +
		 "一、“宏”功能使用说明：\n\n" +
		 "  1、“宏”以文本文档形式，保存在\n" +
		 "     “推箱快手/宏/”文件夹中；\n" +
		 "  2、“宏”不支持嵌套；\n" +
		 "  3、支持行内块（一行多语句）；\n" +
		 "  4、常规动作（Lurd）可以独占一行，\n" +
		 "     也可以分行书写；\n" +
		 "  5、空行将被忽略\n" +
		 "  6、“宏”符号支持全角半角字符，\n" +
		 "     请尽量使用英文（半角）字符，\n" +
		 "     命令中可适当用空格、制表符来\n" +
		 "     美化布局，“宏”符号包括：\n" +
		 "     {}、()、[]、<>、+、@、~ 、^、\n" +
		 "     =、*、?、% 、;、:\n\n" +
		 "二、“宏”命令详解：\n\n" +
		 "* => 块循环符号，两种写法：\n" +
		 "     一是“*n”，即星号后面紧跟一个\n" +
		 "     数字，标识块循环开始，数字为\n" +
		 "     循环次数；二是“*”，即星号独占\n" +
		 "     一行，标识块循环结束。\n" +
		 "{} => 动作符号\n" +
		 "     其内为常规动作（Lurd）字符或数\n" +
		 "     字，若为数字（1--99）会用数字\n" +
		 "     对应的寄存器（1--9 为内部寄存\n" +
		 "     器，在导入对话框中对其进行赋值\n" +
		 "     --动作寄存，而10--99 为玩家自\n" +
		 "     定义的寄存器，可以使用“宏”指\n" +
		 "     令对其赋值--现场动作寄存，引用\n" +
		 "     时，二者同功）中保存的动作串进\n" +
		 "     行替换，花括号后面可以跟数字，\n" +
		 "     也可以不跟，数字标识动作重复次\n" +
		 "     数（0 为无限次），不跟数字时，\n" +
		 "     默认动作执行一次。动作执行时，\n" +
		 "     会遇错即停。\n" +
		 "= => 特别符号，两种用途\n" +
		 "     1、用于首行首字符，强制“宏”\n" +
		 "     代码从关卡初态开始执行，否则，\n" +
		 "     “从当前点”执行，位于其它行时\n" +
		 "     无此作用，且忽略其后面的字符。\n" +
		 "     特别注意，执行“宏”时，导入对\n" +
		 "     话框中的“从当前点”将被忽略；\n" +
		 "     2、用于花括号后面，表示向寄存器\n" +
		 "     赋值，寄存器数字允许“10-99”，\n" +
		 "     等号后面为常规动作字符（Lurd）,\n" +
		 "     赋值后的引用，可参照前面“动作\n" +
		 "     符号”中的说明。\n" +
		 "() => 撤销动作符号\n" +
		 "     其内为数字，表示撤销的单步数。\n" +
		 "[] => 走位符号，绝对坐标走位\n" +
		 "     两种格式，一种是“标尺”格式，\n" +
		 "     即字母数字格式，一种是逗号分隔\n" +
		 "     两个数字的格式，先行后列，行列\n" +
		 "     序号从 0 开始计数。仓管员走位\n" +
		 "     后，不论是否走位成功，新位置都\n" +
		 "     会自动记忆，以便下次走位时作为\n" +
		 "     参照使用。\n" +
		 "+[] => 走位符号，相对坐标走位\n" +
		 "     相对于上次记忆的坐标进行走位，\n" +
		 "     两个坐标做加法，仅支持逗号分隔\n" +
		 "     两个数字一种格式。格式同上，功\n" +
		 "     能同上。\n" +
		 "@[] => 走位符号，相对坐标走位\n" +
		 "     相对于仓管员当前坐标进行走位，\n" +
		 "     两个坐标做加法，仅支持逗号分隔\n" +
		 "     两个数字一种格式。格式同上，功\n" +
		 "     能同上。\n" +
		 "~ => 辅助符号，两种用途，\n" +
		 "     1、跟在“动作”符号（花括号）\n" +
		 "     后面，表示动作时忽略大小写。该\n" +
		 "     符号即可以放在重复数字前，也可\n" +
		 "     以放在重复数字后面；\n" +
		 "     2、跟在“坐标”符号（方括号）\n" +
		 "     后面，表示仅记忆坐标，不走位。\n" +
		 "? => 条件语句符号\n" +
		 "     格式为：? ① = ② : ③ / ④\n" +
		 "     其中 ① 为坐标，② 为关卡元素，\n" +
		 "     ③ 为语句1，④ 为语句2。意思是\n" +
		 "     当 ②（可多个元素连写）包含关卡\n" +
		 "     坐标 ① 上的元素时，执行 ③ 处\n" +
		 "     的语句1，否则执行 ④ 处的语句2\n" +
		 "     ④ 处的语句2可以省略，① 处的坐\n" +
		 "     标不记忆、不走位。注意： ③ 和\n" +
		 "     ④处可以是行内块语句，但是不能\n" +
		 "     再包含“条件语句”，即条件语句\n" +
		 "     不支持嵌套。\n" +
		 "<> => 行内块符号，即一行内多条语句\n" +
		 "     在尖括号中，书写多条单行“宏”语\n" +
		 "     句，各语句间用分号隔开即可。特\n" +
		 "     别的，尖括号后面可以跟数字，也\n" +
		 "     可以不跟，数字标识动作重复的次\n" +
		 "     数（参照花括号，但为 0 时，是\n" +
		 "     执行默认的一次，而非无限次）。\n" +
		 "% => 跳转符号，即：GoTo 语句\n" +
		 "     跳转到冒号为首的标记处执行，标\n" +
		 "     记为非负整数。允许循环块外向循\n" +
		 "     环块内的跳转，不允许循环块内向\n" +
		 "     循环块外的跳转。该语句有风险！\n" +
		 "     特别的：“%*”表示跳出循环块。\n" +
		 ": => 标记符号\n" +
		 "     通常与跳转语句（%）配合使用，\n" +
		 "     冒号后面是一个非负整数，标记要\n" +
		 "     独占一行。\n" +
		 "^ => 断点符号，调试符号，独占一行\n" +
		 "     宏代码执行到此行后，会暂停并转\n" +
		 "     到单步调试模式。注意，循环块内\n" +
		 "     的断点符号不起作用。\n" +
		 "; => 注释符号\n" +
		 "     执行“宏”时，会忽略分号（;）后\n" +
		 "     面的内容，可独占一行，也可放在\n" +
		 "     “宏”语句的后面。";

		 if(myMaps.localCode.equalsIgnoreCase("en")) {
			 s = "Declaration:\n\n" +
					 "The \"Macro\" feature is only supported in forward play mode!\n\n" +

					 "I. Instructions for Using the \"Macro\" Feature:\n\n" +

					 "- Macros are stored as text documents in the \"Boxman/Macros/\" folder.\n" +
					 "- Macros do not support nesting.\n" +
					 "- Inline blocks (multiple statements on one line) are supported.\n" +
					 "- Regular moves (L, u, r, d) can be written on separate lines or on the same line.\n" +
					 "- Empty lines will be ignored.\n" +
					 "- Macro symbols support both full-width and half-width characters." +
					 "  Please use English (half-width) characters whenever possible. Spaces and tabs can be used to improve the layout.\n" +
					 "  Macro symbols include: {}, (), [], <>, +, @, ~, ^, =, *, ?, %, ;, :.\n\n" +

					 "II. Detailed Explanation of Macro Commands:\n\n" +

					 "* (Block Loop Symbol): There are two ways to write this:\n" +
					 "  *n: An asterisk followed immediately by a number indicates the beginning of a block loop. The number represents the number of loop iterations.\n" +
					 "  * (on a separate line): An asterisk on its own line indicates the end of a block loop.\n\n" +

					 "{} (Move Symbol):\n" +
					 "Contains regular move characters (L, u, r, d) or numbers.\n" +
					 "If a number (1-99) is used, it will be replaced with the move sequence stored in the corresponding register.\n" +
					 "Registers 1-9 are internal registers and are assigned values in the import dialog (move storage).\n" +
					 "Registers 10-99 are user-defined registers and can be assigned values using macro commands (live move storage). Both types of registers function the same way when referenced.\n" +
					 "Curly braces can be followed by a number or not. The number indicates the number of times the move sequence should be repeated (0 for infinite repetition). If no number is specified, the move sequence is executed once.\n" +
					 "Move execution will stop immediately upon encountering an error.\n\n" +

					 "= (Special Symbol): Has two purposes:\n" +
					 "When used as the first character on the first line, it forces the macro code to execute from the initial state of the level. Otherwise, the macro will execute from the current position. This function only applies when used on the first line and has no effect on other lines. Characters following the = on the first line are ignored. Please note: The \"Start from Current Position\" option in the import dialog will be ignored when executing a macro.\n" +
					 "When used after curly braces {}, it indicates an assignment to a register. Register numbers from 10 to 99 are allowed. Regular move characters (L, u, r, d) follow the equals sign. Refer to the explanation of the \"Move Symbol\" above for how to reference assigned registers.\n\n" +

					 "() (Undo Move Symbol):\n" +
					 "Contains a number indicating the number of single steps to undo.\n\n" +

					 "[] (Move to Position Symbol - Absolute Coordinates):\n" +
					 "There are two formats: \"ruler\" format (alphanumeric) and comma-separated numbers (row, column). Row and column numbers start from 0.\n" +
					 "After the pusher moves to a position, whether successful or not, the new position is automatically remembered and used as a reference for the next move.\n\n" +

					 "+[] (Move to Position Symbol - Relative Coordinates):\n" +
					 "Moves the pusher relative to the last remembered coordinates. The two coordinates are added together. Only the comma-separated number format is supported. The format and function are the same as above.\n\n" +

					 "@[] (Move to Position Symbol - Relative Coordinates):\n" +
					 "Moves the pusher relative to its current coordinates. The two coordinates are added together. Only the comma-separated number format is supported. The format and function are the same as above.\n\n" +

					 "~ (Auxiliary Symbol): Has two purposes:\n" +
					 "When used after the \"Move\" symbol ({}), it indicates that the case of the move characters should be ignored. This symbol can be placed before or after the repetition number.\n" +
					 "When used after the \"Coordinate\" symbol ([]), it indicates that only the coordinates should be remembered, and the pusher should not move.\n\n" +

					 "? (Conditional Statement Symbol):\n" +
					 "Format: ? ① = ② : ③ / ④\n" +
					 "①: Coordinates.\n" +
					 "②: Level elements (multiple elements can be written together).\n" +
					 "③: Statement 1.\n" +
					 "④: Statement 2 (optional).\n" +
					 "If ② contains the element at coordinates ①, statement 1 (③) is executed. Otherwise, statement 2 (④) is executed. Statement 2 can be omitted. The coordinates in ① are not remembered or used for movement. Note: Statements ③ and ④ can be inline block statements, but they cannot contain conditional statements (conditional statements cannot be nested).\n\n" +

					 "<> (Inline Block Symbol):\n" +
					 "Allows writing multiple single-line macro commands within angle brackets, separated by semicolons.\n" +
					 "A number can be added after the closing angle bracket to specify the number of times the block should be repeated (similar to curly braces, but 0 means execute once, not infinitely).\n\n" +

					 "% (Jump Symbol): GoTo statement.\n" +
					 "Jumps to the label indicated by a colon followed by a non-negative integer. Jumps from outside a loop to inside a loop are allowed, but jumps from inside a loop to outside are not. This statement can be risky!\n" +
					 "Special case: %* indicates jumping out of the current loop.\n\n" +

					 ": (Label Symbol):\n" +
					 "Usually used with the jump statement (%). A colon is followed by a non-negative integer and must be on a separate line.\n\n" +

					 "^ (Breakpoint Symbol): Debugging symbol.\n" +
					 "When the macro code execution reaches this line, it will pause and switch to single-step debugging mode. Note: Breakpoint symbols inside loops have no effect.\n\n" +

					 "; (Comment Symbol):\n" +
					 "When executing a macro, everything after a semicolon (;) is ignored. Can be used on a separate line or at the end of a macro statement. ";
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
