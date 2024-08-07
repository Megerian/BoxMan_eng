package my.boxman;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class Help extends Activity {
	 private static TextView tv_help = null;

	 int m_Num = 0;
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.help);

	    //接收数据
	    Bundle bundle = this.getIntent ().getExtras ();
	    m_Num = bundle.getInt ("m_Num");  //指示调用者是谁

		 //开启标题栏的返回键
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

		tv_help = (TextView)this.findViewById(R.id.tvHelp);

		 StringBuilder s = new StringBuilder();

		 if (m_Num == 1) {  // 推关卡界面调用
		   	 setTitle(getString(R.string.title_operation_instructions));
			 s.append('\n')
				 .append(getString(R.string.single_click_empty_space)).append('\n')
				 .append(getString(R.string.single_click_player)).append('\n')
				 .append(getString(R.string.single_click_on_box)).append('\n')
				 .append('\n')
				 .append(getString(R.string.double_click_player)).append('\n')
				 .append(getString(R.string.double_click_box)).append('\n')
				 .append(getString(R.string.double_click_wall_outside_wall)).append('\n')
				 .append('\n')
				 .append(getString(R.string.long_press_wall)).append('\n')
				 .append(getString(R.string.long_press_on_box_not_on_a_point)).append('\n')
				 .append(getString(R.string.long_press_on_box_on_point)).append('\n')
				 .append(getString(R.string.long_press_on_empty_space)).append('\n')
				 .append(getString(R.string.long_press_on_player)).append('\n')
				 .append('\n')
				 .append('\n')
				 .append(getString(R.string.single_click_on_top_row_puzzle_number)).append('\n')
				 .append(getString(R.string.single_click_on_top_row_moves)).append('\n')
				 .append(getString(R.string.single_click_on_top_row_pushes)).append('\n')
				 .append(getString(R.string.single_click_on_top_row_cursor_indicator)).append('\n')
				 .append(getString(R.string.long_press_on_quick_background_color_change)).append('\n')
				 .append('\n')
				 .append(getString(R.string.long_press_on_top_row_puzzle_number)).append('\n')
				 .append(getString(R.string.long_press_on_top_row_moves)).append('\n')
				 .append(getString(R.string.long_press_on_top_row_pushes)).append('\n')
				 .append(getString(R.string.long_press_on_top_row_cursor_indicator)).append('\n')
				 .append('\n')
				 .append('\n')
				 .append(getString(R.string.long_press_on_bottom_row_back_button)).append('\n')
				 .append(getString(R.string.long_press_on_bottom_row_forward_button)).append('\n')
				 .append(getString(R.string.long_press_on_bottom_row_rotate_button)).append('\n')
				 .append(getString(R.string.long_press_on_bottom_row_more_button)).append('\n')
				 .append('\n')
				 .append('\n')
				 .append(getString(R.string.reminder_when_fullsrceen));

		 } else if (m_Num == 2) {  // 创编关卡界面调用
			 setTitle(getString(R.string.title_operation_instructions));
			 s.append('\n')
				 .append(getString(R.string.help_top_row)).append('\n')
				 .append('\n')
				 .append(getString(R.string.single_click_on_materials_in_the_top_row)).append('\n')
				 .append(getString(R.string.long_press_on_materials_in_the_top_row)).append('\n')
				 .append(getString(R.string.single_click_on_puzzle_dimensions_in_the_top_row)).append('\n')
				 .append(getString(R.string.long_press_on_puzzle_dimensions_in_the_top_row)).append('\n')
				 .append(getString(R.string.long_press_on_more_button)).append('\n')
				 .append('\n')
				 .append(getString(R.string.in_block_selection_mode)).append('\n')
				 .append(getString(R.string.single_click_on_puzzle_map)).append('\n')
				 .append(getString(R.string.double_click_on_boundary)).append('\n')
				 .append('\n')
				 .append(getString(R.string.in_editing_mode)).append('\n')
				 .append(getString(R.string.editing_mode_single_click_on_puzzle_map)).append('\n')
				 .append(getString(R.string.editing_mode_double_click_on_boundary)).append('\n')
				 .append('\n')
				 .append(getString(R.string.reminder_blocks));
		 } else if (m_Num == 3) {   // 相似关卡界面调用
			 setTitle(getString(R.string.title_operation_instructions));
			 s.append('\n')
				 .append(getString(R.string.double_click_on_puzzle_map)) ;
		 } else if (m_Num == 4) {   // 导入说明
			 setTitle(getString(R.string.title_import_instructions));
			 StringBuilder append = s.append('\n')
					 .append(getString(R.string.import_specification)).append('\n')
					 .append(getString(R.string.ony_forward_macros)).append('\n')
					 .append(getString(R.string.reverse_push1)).append('\n')
					 .append(getString(R.string.reverse_push2)).append('\n')
					 .append(getString(R.string.reverse_push3)).append('\n')
					 .append(getString(R.string.lurd_1)).append('\n')
					 .append(getString(R.string.lurd_2)).append('\n')
					 .append(getString(R.string.lurd_3)).append('\n')
					 .append(getString(R.string.edit_macros_1)).append('\n')
					 .append(getString(R.string.edit_macros_2)).append('\n')
					 .append(getString(R.string.edit_macros_3)).append('\n')
					 .append(getString(R.string.from_current_point_1)).append('\n')
					 .append(getString(R.string.from_current_point_2)).append('\n')
					 .append(getString(R.string.from_current_point_3)).append('\n')
					 .append(getString(R.string.from_current_point_4)).append('\n')
					 .append(getString(R.string.from_current_point_5)).append('\n')
					 .append(getString(R.string.six_1)).append('\n')
					 .append(getString(R.string.six_2)).append('\n')
					 .append(getString(R.string.six_3)).append('\n')
					 .append(getString(R.string.six_4)).append('\n')
					 .append(getString(R.string.six_5)).append('\n').append('\n')
					 .append(getString(R.string.coordinates_in_reverse_play)).append('\n').append('\n')
					 .append(getString(R.string.reverse_coordinates_1)).append('\n')
					 .append(getString(R.string.reverse_coordinates_2)).append('\n')
					 .append(getString(R.string.reverse_coordinates_3)).append('\n')
					 .append(getString(R.string.reverse_coordinates_4)).append('\n')
					 .append(getString(R.string.reverse_coordinates_5)).append('\n')
					 .append(getString(R.string.reverse_coordinates_6)).append('\n')
					 .append(getString(R.string.reverse_coordinates7)).append('\n')
					 .append(getString(R.string.reverse_coordinates_no_default_coordinates_1)).append('\n')
					 .append(getString(R.string.reverse_coordinates_no_default_coordinates_2)).append('\n')
					 .append(getString(R.string.reverse_coordinates_no_default_coordinates_3)).append('\n')
					 .append(getString(R.string.reverse_coordinates_no_default_coordinates_4)).append('\n')
					 .append(getString(R.string.invalid_coordinates_1)).append('\n')
					 .append(getString(R.string.invalid_coordinates_2)).append('\n')
					 .append(getString(R.string.invalid_coordinates_3));
		 } else if (m_Num == 5) {   // 宏功能说明
			 setTitle(getString(R.string.description_of_macro_function));

			 if(myMaps.localCode.equalsIgnoreCase("zh")) {
				 s.append('\n')
						 .append("声明：仅正推支持“宏”功能！\n").append('\n')
						 .append("一、“宏”功能使用说明：\n").append('\n')
						 .append("  1、“宏”以文本文档形式，保存在").append('\n')
						 .append("     “推箱快手/宏/”文件夹中；").append('\n')
						 .append("  2、“宏”不支持嵌套；").append('\n')
						 .append("  3、支持行内块（一行多语句）；").append('\n')
						 .append("  4、常规动作（Lurd）可以独占一行，").append('\n')
						 .append("     也可以分行书写；").append('\n')
						 .append("  5、空行将被忽略").append('\n')
						 .append("  6、“宏”符号支持全角半角字符，").append('\n')
						 .append("     请尽量使用英文（半角）字符，").append('\n')
						 .append("     命令中可适当用空格、制表符来").append('\n')
						 .append("     美化布局，“宏”符号包括：").append('\n')
						 .append("     {}、()、[]、<>、+、@、~ 、^、").append('\n')
						 .append("     =、*、?、% 、;、:").append('\n').append('\n')
						 .append("二、“宏”命令详解：").append('\n').append('\n')
						 .append("* => 块循环符号，两种写法：").append('\n')
						 .append("     一是“*n”，即星号后面紧跟一个").append('\n')
						 .append("     数字，标识块循环开始，数字为").append('\n')
						 .append("     循环次数；二是“*”，即星号独占").append('\n')
						 .append("     一行，标识块循环结束。").append('\n')
						 .append("{} => 动作符号").append('\n')
						 .append("     其内为常规动作（Lurd）字符或数").append('\n')
						 .append("     字，若为数字（1--99）会用数字").append('\n')
						 .append("     对应的寄存器（1--9 为内部寄存").append('\n')
						 .append("     器，在导入对话框中对其进行赋值").append('\n')
						 .append("     --动作寄存，而10--99 为玩家自").append('\n')
						 .append("     定义的寄存器，可以使用“宏”指").append('\n')
						 .append("     令对其赋值--现场动作寄存，引用").append('\n')
						 .append("     时，二者同功）中保存的动作串进").append('\n')
						 .append("     行替换，花括号后面可以跟数字，").append('\n')
						 .append("     也可以不跟，数字标识动作重复次").append('\n')
						 .append("     数（0 为无限次），不跟数字时，").append('\n')
						 .append("     默认动作执行一次。动作执行时，").append('\n')
						 .append("     会遇错即停。").append('\n')
						 .append("= => 特别符号，两种用途").append('\n')
						 .append("     1、用于首行首字符，强制“宏”").append('\n')
						 .append("     代码从关卡初态开始执行，否则，").append('\n')
						 .append("     “从当前点”执行，位于其它行时").append('\n')
						 .append("     无此作用，且忽略其后面的字符。").append('\n')
						 .append("     特别注意，执行“宏”时，导入对").append('\n')
						 .append("     话框中的“从当前点”将被忽略；").append('\n')
						 .append("     2、用于花括号后面，表示向寄存器").append('\n')
						 .append("     赋值，寄存器数字允许“10-99”，").append('\n')
						 .append("     等号后面为常规动作字符（Lurd）,").append('\n')
						 .append("     赋值后的引用，可参照前面“动作").append('\n')
						 .append("     符号”中的说明。").append('\n')
						 .append("() => 撤销动作符号").append('\n')
						 .append("     其内为数字，表示撤销的单步数。").append('\n')
						 .append("[] => 走位符号，绝对坐标走位").append('\n')
						 .append("     两种格式，一种是“标尺”格式，").append('\n')
						 .append("     即字母数字格式，一种是逗号分隔").append('\n')
						 .append("     两个数字的格式，先行后列，行列").append('\n')
						 .append("     序号从 0 开始计数。仓管员走位").append('\n')
						 .append("     后，不论是否走位成功，新位置都").append('\n')
						 .append("     会自动记忆，以便下次走位时作为").append('\n')
						 .append("     参照使用。").append('\n')
						 .append("+[] => 走位符号，相对坐标走位").append('\n')
						 .append("     相对于上次记忆的坐标进行走位，").append('\n')
						 .append("     两个坐标做加法，仅支持逗号分隔").append('\n')
						 .append("     两个数字一种格式。格式同上，功").append('\n')
						 .append("     能同上。").append('\n')
						 .append("@[] => 走位符号，相对坐标走位").append('\n')
						 .append("     相对于仓管员当前坐标进行走位，").append('\n')
						 .append("     两个坐标做加法，仅支持逗号分隔").append('\n')
						 .append("     两个数字一种格式。格式同上，功").append('\n')
						 .append("     能同上。").append('\n')
						 .append("~ => 辅助符号，两种用途，").append('\n')
						 .append("     1、跟在“动作”符号（花括号）").append('\n')
						 .append("     后面，表示动作时忽略大小写。该").append('\n')
						 .append("     符号即可以放在重复数字前，也可").append('\n')
						 .append("     以放在重复数字后面；").append('\n')
						 .append("     2、跟在“坐标”符号（方括号）").append('\n')
						 .append("     后面，表示仅记忆坐标，不走位。").append('\n')
						 .append("? => 条件语句符号").append('\n')
						 .append("     格式为：? ① = ② : ③ / ④").append('\n')
						 .append("     其中 ① 为坐标，② 为关卡元素，").append('\n')
						 .append("     ③ 为语句1，④ 为语句2。意思是").append('\n')
						 .append("     当 ②（可多个元素连写）包含关卡").append('\n')
						 .append("     坐标 ① 上的元素时，执行 ③ 处").append('\n')
						 .append("     的语句1，否则执行 ④ 处的语句2").append('\n')
						 .append("     ④ 处的语句2可以省略，① 处的坐").append('\n')
						 .append("     标不记忆、不走位。注意： ③ 和").append('\n')
						 .append("     ④处可以是行内块语句，但是不能").append('\n')
						 .append("     再包含“条件语句”，即条件语句").append('\n')
						 .append("     不支持嵌套。").append('\n')
						 .append("<> => 行内块符号，即一行内多条语句").append('\n')
						 .append("     在尖括号中，书写多条单行“宏”语").append('\n')
						 .append("     句，各语句间用分号隔开即可。特").append('\n')
						 .append("     别的，尖括号后面可以跟数字，也").append('\n')
						 .append("     可以不跟，数字标识动作重复的次").append('\n')
						 .append("     数（参照花括号，但为 0 时，是").append('\n')
						 .append("     执行默认的一次，而非无限次）。").append('\n')
						 .append("% => 跳转符号，即：GoTo 语句").append('\n')
						 .append("     跳转到冒号为首的标记处执行，标").append('\n')
						 .append("     记为非负整数。允许循环块外向循").append('\n')
						 .append("     环块内的跳转，不允许循环块内向").append('\n')
						 .append("     循环块外的跳转。该语句有风险！").append('\n')
						 .append("     特别的：“%*”表示跳出循环块。").append('\n')
						 .append(": => 标记符号").append('\n')
						 .append("     通常与跳转语句（%）配合使用，").append('\n')
						 .append("     冒号后面是一个非负整数，标记要").append('\n')
						 .append("     独占一行。").append('\n')
						 .append("^ => 断点符号，调试符号，独占一行").append('\n')
						 .append("     宏代码执行到此行后，会暂停并转").append('\n')
						 .append("     到单步调试模式。注意，循环块内").append('\n')
						 .append("     的断点符号不起作用。").append('\n')
						 .append("; => 注释符号").append('\n')
						 .append("     执行“宏”时，会忽略分号（;）后").append('\n')
						 .append("     面的内容，可独占一行，也可放在").append('\n')
						 .append("     “宏”语句的后面。");
			 } else {
				 s.append( "Declaration:\n\n" +
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
						 "When executing a macro, everything after a semicolon (;) is ignored. Can be used on a separate line or at the end of a macro statement. ");
			 }
		 } else if (m_Num == 6) {   // 图片识别说明
			 setTitle(getString(R.string.title_operation_instructions));
			 if(myMaps.localCode.equalsIgnoreCase("zh")) {
				 s.append('\n')
						 .append("  图像识别，是利用关卡截图，自动识别出其 XSB，属于创编关卡的辅助功能。\n").append('\n')
						 .append("一般步骤：\n").append('\n')
						 .append("  1、边框定位：").append('\n')
						 .append("     边框线上带有圆形“指示灯”，粉红色为“点亮”状态，表示该边线被选中，长按可以“点亮”（当没有选择底行的 XSB 元素时，也可以单击“点亮”）。底行的“上下左右”按钮对被选中的边线进行微调定位，也可以在“指示灯”附近双击进行微调；“长按”指示灯（此时，指示灯会闪烁）可以直接拖动当前边线；边框线采用了“黑白”双色线条，对位的最佳效果是以白线“压边”。").append('\n')
						 .append("  2、确定横向格子数：").append('\n')
						 .append("     当边框确定好后，就可以利用顶行的“增”、“减”按钮调整关卡横向的格子数，而纵向格子数会自动计算。").append('\n')
						 .append("  3、识别：").append('\n')
						 .append("     当格线调整好之后，先点选底行的 XSB“元素”，然后点击图片中的相应格子，快手会自动启动“识别”。").append('\n')
						 .append("  4、识别设置：").append('\n')
						 .append("     当识别效果不理想时，可以通过顶行的“度”按钮，调整识别“相似度”，会有一定程度的改善。").append('\n')
						 .append("  5、编辑：").append('\n')
						 .append("     对于少量的关卡元素，也可以直接手动编辑而不做自动识别。通过点击顶行的“识别”或“编辑”菜单项进行模式切换。在“编辑”模式下，先点选底行的 XSB“元素”，然后点击“格子”即可。特别的，长按底行的 XSB“元素”，会有更多功能，具体不做详述。");
			 } else {
				 s.append('\n').append(
						 "General Steps:\n" +
						 "1. Border Positioning:\n" +
						 "  Border lines have circular \"indicator lights.\" Pink indicates an \"active\" state, meaning the border line is selected." +
						 " Long-press to activate a border line (you can also single-tap to activate if no XSB element is selected in the bottom row)." +
						 " Use the \"Up,\" \"Down,\" \"Left,\" and \"Right\" buttons in the bottom row to fine-tune the position of the selected border line." +
						 " You can also double-tap near an \"indicator light\" for fine adjustments." +
						 " Long-pressing an indicator light (it will flash) allows you to directly drag the corresponding border line." +
						 " Border lines use a \"black and white\" double-line design." +
						 " The best alignment is achieved when the white line \"overlaps\" the edge of the level.\n\n" +

						 "2. Determine the Number of Horizontal Grids:\n" +
						 " Once the border is set, use the \"Increase\" and \"Decrease\" buttons in the top row to adjust the number of horizontal grids in the level." +
						 " The number of vertical grids will be calculated automatically.\n\n" +

						 "3. Recognition:\n" +
						 "After adjusting the grid lines, first select the XSB \"element\" in the bottom row, then tap the corresponding grid in the image." +
						 " The app will automatically start the recognition process.\n\n" +

						 "4. Recognition Settings:\n" +
						 "If the recognition results are not satisfactory, you can adjust the \"similarity\" level using the \"Degree\" button in the top row." +
						 " This may improve the results.\n\n" +

						 "5. Editing:\n" +
						 "For a small number of level elements, you can also manually edit them instead of using automatic recognition." +
						 " Switch between modes by tapping the \"Recognition\" or \"Edit\" menu items in the top row." +
						 " In \"Edit\" mode, first select the XSB \"element\" in the bottom row, then tap the \"grid.\"\n" +
						 " Special note: Long-pressing the XSB \"element\" in the bottom row will provide more functions (details not provided here)."
				 );
			 }
		 } else {    // 主界面调用
			 if(myMaps.localCode.equalsIgnoreCase("zh")) {
				 s.append("====== 特别提醒 ======").append('\n')
				 .append('\n')
				 .append("  在重要“节点”请及时做好关卡库（DataBase/Boxman.db）的备份，尤其在每次升级“快手”、或在做较多较大关卡集导入的之前，以防不测！！！").append('\n')
				 .append('\n')
				 .append("===== 下载与交流 =====").append('\n')
				 .append('\n')
				 .append("网站：sokoban.cn").append('\n')
				 .append("QQ群：92017135").append('\n')
				 .append('\n')
				 .append("======== 简介 ========").append('\n')
				 .append('\n')
				 .append("  1、“快手”中，关卡、答案采用(XSB+Lurd)标准格式，关卡（集）文档的扩展名支持“XSB、TXT、SOK、TXZ”等）；").append('\n')
				 .append("  2、“快手”使用 SQLite 数据库管理关卡和答案数据，简称“关卡库”，关卡库及“快手”的其他数据均放置在手机内存卡的“/storage/emulated/0/推箱快手/”下，“DataBase/BoxMan.db”即为关卡库，关卡库中的数据不可手动修改，否则可能导致 APP 崩溃！但可以删除或改名（文件夹或关卡库），删除或改名后，关卡库（“快手”自带的原始数据）会自动生成；").append('\n')
				 .append("  3、“快手”支持自设皮肤，玩家可将自定义皮肤（PNG 图片）文档复制到“皮肤/”下，然后在 APP 里设置更换皮肤即可，“defskin.png”为默认皮肤（尺寸为 200 x 400，此文件可以删除，删除后会自动生成）为“快手”自带的默认皮肤，玩家可以参照其格式自己绘制，注意部分元素使用了透明背景。另外，“快手”还支持简版皮肤（200 x 350，仅含两个方向的仓管员图元）和超简皮肤（200 x 150，仅含一个墙体及两个方向的仓管员图元）；").append('\n')
				 .append("  4、“快手”支持关卡扩展，通常称为“导入”，导入有单关卡的导入；有文档方式的导入，有剪切板方式的导入；还可以通过关卡编辑中的提交导入。在某些导入方式中，还可同时导入关卡答案以及批量导入；").append('\n')
				 .append("  5、导入关卡集，需先将关卡集文档复制到“导入/”下，然后在首界面的菜单中，选择“导入...”即可；").append('\n')
				 .append("  6、对于导入、通过 YASS 得到或优化的答案，快手会自动将“[导入]”或“[YASS]”记入该答案的“备注”中；").append('\n')
				 .append("  7、“快手”支持“状态”与“答案”保存和导出，导出的文档，均放在“导出/”文件夹下；").append('\n')
				 .append("  8、关卡的扩展和删除，只针对玩家自己扩展的关卡，即：“关卡扩展”分组；").append('\n')
				 .append("  9、快手编辑功能中，导入支持关卡 XSB 导入，同时支持 Lurd 导入（即：通过答案 Lurd 倒推出关卡 XSB）。另外，快手还支持关卡图的自动识别，玩家可以先将关卡截图复制到“关卡图/”文件夹中，然后，通过关卡编辑功能中的“图像识别”，自动识别出关卡 XSB；").append('\n')
				 .append("  10、“快手”支持的关卡尺寸为：3--100行、3--100列，对尺寸超标或不足、箱子与目标不符、仓管员数目非法的，均视为无效关卡且不予接收，用灰色预览图标示占位；").append('\n')
				 .append("  11、“快手”支持答案（或动作）合成 GIF 动画，其中：“现场皮肤”动画，可以将名称为“banner.png”图片放到“推箱快手/”一级文件夹下（建议尺寸控制在“200  x 42”以内，且需在快手启动前放置才有效），作为自定义动画水印，默认水印为“sokoban.cn”网站域名，“现场皮肤”动画同时支持人工箱子编号和标尺，以便教学；而“固定皮肤”，默认水印为“推箱子群”的QQ号码，自定义水印，自动取提交比赛答案的玩家“姓名”。").append('\n')
				 .append('\n')
				 .append("====== 最近更新 ======").append('\n')
				 .append('\n')
				 .append("BoxMan9.99u~：2024-03-27").append('\n')
				 .append("1、增加“双击编号”开关项。").append('\n')
//				 .append("1、为保存“状态”的关卡增加颜色提示；").append('\n')
//				 .append("2、为关卡编辑增加批量修改功能。").append('\n')
//				 .append("2、为关卡增加“Festival求解”选项。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99t：2024-03-09").append('\n')
				 .append("1、导出现场的XSB时，更新到兼容互动双推；").append('\n')
				 .append("2、支持比赛网站的自定义。").append('\n')
//				 .append("2、为关卡增加“Festival求解”选项。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99s：2024-01-27").append('\n')
				 .append("1、调整开关设置项“推关卡时显示时间提示”为默认打开；").append('\n')
				 .append("2、修正因仓管员坐标错误引起的逆推动作不能导入问题；").append('\n')
				 .append("3、为关卡编辑模块中的“更多”增加“离开”选项；").append('\n')
				 .append("4、解决新手机推关卡界面和关卡编辑界面中“更多”按钮只能使用一次的问题；").append('\n')
				 .append("5、更新SDK到v33重新编译。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99r：2023-02-14").append('\n')
				 .append("1、为“推箱”界面的“开关设置项”添加恢复“默认”按钮；").append('\n')
				 .append("2、修正“对角”死锁误判问题（gl）。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99q：2023-02-08").append('\n')
				 .append("1、增加对第四比赛关卡的支持。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99n：2022-03-30").append('\n')
				 .append("1、修正因优化皮肤效果造成的逆推状态下仓管员正推原始位置右侧图块显示异常的问题（火炬手）；").append('\n')
				 .append("2、为“推箱”界面添加“退出”菜单。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99m：2022-03-11").append('\n')
				 .append("1、修正因提前拆分皮肤造成的查看重复关卡的关于后，关卡图出现的半透明问题（冥想修行、跳跳灵）。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99k：2022-03-09").append('\n')
				 .append("1、优化皮肤效果。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99j：2022-02-22").append('\n')
				 .append("1、修正在关卡浏览界面从剪切板导入1个关卡后闪退bug。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99i：2022-02-16").append('\n')
				 .append("1、修正逆推中没有若仓管员长按“地板”会闪退的bug（火炬手）。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99h：2021-05-25").append('\n')
				 .append("1、为增强画图效果，从皮肤中取墙顶图块和画墙顶时，从四周向内各收缩一个像素，避免墙顶图块周边像素的杂色影响。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99g：2021-03-09").append('\n')
				 .append("1、优化导入动作时，剪切板的运用；").append('\n')
				 .append("2、增加网锁提示需在非“互动双推”模式下的限制;").append('\n')
				 .append("3、完善死锁检测功能，提升对“无边界”关卡的支持。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99f：2020-09-4").append('\n')
				 .append("1、将“逆推时使用正推目标点”模式命名为“定位双推”模式；").append('\n')
				 .append("2、解决高版本安卓（V10+）在导入动作时，不能自动加载剪切板的问题。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99e：2020-06-05").append('\n')
				 .append("1、修正“背景时间定时器”延迟时间过小可能引发的闪退问题（老夫子）。").append('\n')
				 .append('\n')
				 .append("BoxMan9.99d：2020-06-04").append('\n')
				 .append("1、修正长按关卡集标题的“详细”中关卡数字统计错误的Bug（小珏）;").append('\n')
				 .append("2、增加对自动保存导入答案的支持（但是，有时保存提示不能正常显示出来）；").append('\n')
				 .append("3、游戏时，允许背景上显示当前时间；").append('\n')
				 .append("4、修正创建先关卡集时，误判命名合法性的Bug（守望pj）；").append('\n')
				 .append("5、将“即景”模式重新命名为“互动双推”模式（anian版主）。").append('\n')
				 .append('\n')
				 .append("BoxMan9.98：2019-11-22").append('\n')
                 .append("1、修正导入逆推动作时偶尔闪退的Bug；").append('\n')
                 .append("2、加载比赛的关卡和答案列表时，增加对第二副关的支持。").append('\n')
				 .append('\n')
				 .append("BoxMan9.97：2019-10-17").append('\n')
				 .append("1、优化“互动双推”模式；").append('\n')
				 .append("2、完善比赛答案提交功能（anian版主）；").append('\n')
                 .append("3、优化关卡截图文档列表按创建时间排序，把最新的列在最前面（anian版主）；").append('\n')
				 .append("4、修正个别手机上，状态栏和菜单字体不清晰问题（Xiao Fei xia、不愿透露姓名）；").append('\n')
                 .append("5、更新逆推中的仓管员坐标[0, 0]为起点到[1, 1]为起点（anian版主）；").append('\n')
                 .append("6、修正关卡浏览界面处于“显示标题”模式下，长按关卡的“移动到...”功能有时不能正确执行的bug（anian版主）；").append('\n')
				 .append("7、修正逆推动作导入时，偶尔出现多个仓管员的bug；").append('\n')
				 .append("8、其它优化。").append('\n')
				 .append('\n')
				 .append("BoxMan9.96：2019-6-29").append('\n')
				 .append("1、增加关卡图默认的最大缩放倍数（关雎）；").append('\n')
				 .append("2、修复“对角死锁”检测时偶尔误报的bug（天）；").append('\n')
				 .append("3、其它优化。").append('\n')
				 .append('\n')
				 .append("BoxMan9.95：2019-4-26").append('\n')
				 .append("1、新增导入往期比赛关卡功能（无极）；").append('\n')
				 .append("2、新增奇偶位地板格显示功能，并为其增设开关选项，更改“死锁嗅探”开关快键（长按顶行关卡序号）为“奇偶格位显示”开关快键，同时，允许玩家在“设置”中对奇偶格的明暗度进行调整（云淡风清、cjcjcj）；").append('\n')
				 .append("3、修复编辑关卡时，最右边一列的箱子和目标点统计不到的bug；").append('\n')
				 .append("4、优化状态保存功能（状态重复时，仅更新一下时间，以确保下次排到最前面）；").append('\n')
				 .append("5、修复“对角死锁”检测时偶尔误报的bug（棉花糖ONE）；").append('\n')
				 .append("6、修复使用简单皮肤时，重复关卡详细资料中关卡图显示不完整的bug（没有箱子、目标点和人）；").append('\n')
				 .append("7、其它优化。").append('\n')
				 .append('\n')
				 .append("BoxMan9.94：2019-3-29").append('\n')
				 .append("1、答案列表加入“时间优先”排序，时间相同时“移动优先”（不愿透露姓名）；").append('\n')
				 .append("2、创编关卡的“块选模式”下，双击边界，关卡尺寸自动增加（anian版主）；").append('\n')
				 .append("3、进一步完善识别中的编辑功能；").append('\n')
				 .append("4、简单优化“帮助”功能；").append('\n')
				 .append("5、导出时的 GIF 同时支持“自动箱子编号”和“人工箱子编号”（anian版主）；").append('\n')
				 .append("6、优化图像识别中的自定义图片位置功能（anian版主）；").append('\n')
				 .append("7、通过改变“标尺”提示框中文字的颜色，提示其可能的“半位”的奇偶性（20603大师）；").append('\n')
				 .append("8、其它优化。").append('\n')
				 .append('\n')
				 .append("BoxMan9.93：2019-3-1").append('\n')
				 .append("1、关卡图像的自动识别换用艾嘉发现的新算法，效率提升巨大；识别模块包含两种工作模式，即：“识别”模式和“编辑”模式；识别完成后送入“关卡编辑”时，会截取图片作为编辑参照（编辑中，可双击顶行关闭或开启参照图）；边框和格子大小的调整方式变动较大，底行的“微调”按钮增加长按操作等（爱伽斯顿、anian版主）；").append('\n')
				 .append("2、关卡的答案列表增加“移动优先”或“推动优先”重排功能（每次打开时默认“移动优先”，长按切换）；").append('\n')
				 .append("3、由于未解关卡在打开时会自动打开并定位到上次保存的状态，所以增设“自动加载最新状态”临时开关选项（先回到关卡初态，再“长按”仓管员），以避免相似关卡无法用切换法比对的问题；").append('\n')
				 .append("4、改用“GBK”编码提交比赛答案（微风中的歌声）；").append('\n')
				 .append("5、进一步完善“闭口对角”死锁嗅探功能；").append('\n')
				 .append("6、其它优化。").append('\n')
				 .append('\n')
				 .append("BoxMan9.92：2019-1-29").append('\n')
				 .append("1、优化相似关卡搜索功能（anian版主）；").append('\n')
				 .append("2、优化关卡图像的自动识别功能；").append('\n')
				 .append("3、修正文档导入中“GBK”编码被提示为“UTF-8”的错误（不愿透露姓名）；").append('\n')
				 .append("4、修复创编关卡没有保存时，呼叫关卡资料和尝试试推闪退的bug（不愿透露姓名）；").append('\n')
				 .append("5、修复对角死锁检测的一处bug（黑龙江毕景鹏）。").append('\n')
				 .append('\n')
				 .append("BoxMan9.91：2019-1-25").append('\n')
				 .append("1、优化“冻结”（原“方型”和“之字型”）死锁检测代码，创建独立类；").append('\n')
				 .append("2、优化“闭锁对角”死锁检测代码；").append('\n')
				 .append("3、优化关卡浏览功能，同时，此处增设“添加关卡”、“导出”、改变“每行图标个数”等功能（麦英、爱伽斯顿）；").append('\n')
				 .append("4、优化“创编关卡”的尺寸设定和修改功能（爱伽斯顿）；").append('\n')
				 .append("5、优化关卡解析功能：解析时，忽略（直接舍弃，不再视作无效关卡记录到备注中）行数小于 3 或者列数小于 3 的关卡；对于连续的“标题”、“作者”等信息，只解析第一个；对于超尺寸关卡，视作无效关卡，并将其记录到备注中，同时，其标题强制为“无效关卡”，以便查询；在保留关卡外围造型的前提下，自动做关卡的“瘦化”处理（去除关卡外围无用的“空白”）（麦英、爱伽斯顿、anian版主）；").append('\n')
				 .append("6、首界面、关卡浏览界面，增加“详细...”菜单项，显示关卡集或关卡的说明信息等（爱伽斯顿）;").append('\n')
				 .append("7、修正逆推中“点位不足”死锁检测的bug（爱伽斯顿）；").append('\n')
				 .append("8、修正关卡浏览界面关卡不能正确复制的bug（麦英）；").append('\n')
				 .append("9、新建关卡集时，快手会为玩家推荐一个名称；移动、复制、提交关卡时，在关卡集列表的末尾，快手也会推荐一个新建的关卡集备用（爱伽斯顿、anian版主）；").append('\n')
				 .append("10、推关卡时，对于导入的常规动作（不包含任何“宏”命令），自动忽略大小写执行（20603、anian版主）；").append('\n')
				 .append("11、打开关卡时，若有答案，自动加载答案；否则，若有状态，自动加载到最新状态（爱伽斯顿）；").append('\n')
				 .append("12、强制用墙壁替换关卡外围造型中的箱子，即只允许使用墙壁做造型；").append('\n')
				 .append("13、强制 BoxWorld 关卡集的第一个关卡至少保留 1 个答案（DB原因，答案表中若无记录，可能会发生不可预知的bug）；").append('\n')
				 .append("14、导入导出时，使用“导入/”、“导出/”文件夹，不再使用“关卡扩展/”文件夹；对于超长无法写入DB的答案，将以文档形式写入“超长答案/”文件夹").append('\n')
				 .append("15、比赛答案提交列表、关卡浏览菜单等其它方面的优化；").append('\n')
				 .append("16、导入文档关卡时，增加“GBK”编码选项（小珏）；").append('\n')
				 .append("17、增加关卡图的自动识别功能（爱伽斯顿、anian版主）。").append('\n')
				 .append('\n')
				 .append("BoxMan9.90：2018-12-24").append('\n')
				 .append("1、优化最后一步的“撤销”功能；").append('\n')
				 .append("2、完善死锁检测功能，以移植 JSoko 的“闭锁对角”（原“双 L”型）死锁检测为主，创建独立类；").append('\n')
				 .append("3、将探路算法中的 parent 等变量由原来的 byte[][] 类型改变为 short[][] 类型，已修复因穿越深度过大时，超过 byte 类型最大值的 bug。");
			 } else {
				 s.append("====== Special Reminder ======").append('\n')
				 .append('\n')
				 .append("  Please back up the checkpoint database (DataBase/Boxman.db) promptly at important 'nodes,' especially before upgrading 'Boxman' or importing many large level sets, to prevent unexpected issues!!!").append('\n')
				 .append('\n')
				 .append("===== Download and Community =====").append('\n')
				 .append('\n')
				 .append("Website: sokoban.cn").append('\n')
				 .append("QQ Group: 92017135").append('\n')
				 .append('\n')
				 .append("======== Introduction ========").append('\n')
				 .append('\n')
				 .append("  1. In 'Boxman,' levels and solutions use the (XSB+Lurd) standard format. Level (set) files support extensions like 'XSB, TXT, SOK, TXZ,' etc.;").append('\n')
				 .append("  2. 'Boxman' uses an SQLite database to manage level and solution data, referred to as the 'Level Database.' The level database and other data from 'Boxman' are stored under '/storage/emulated/0/Boxman/' on the phone's memory card. 'DataBase/BoxMan.db' is the level database, and data in the level database should not be manually modified, as this may cause the app to crash! However, you can delete or rename (the folder or the level database), and the level database (the original data that comes with 'Boxman') will automatically regenerate;").append('\n')
				 .append("  3. 'Boxman' supports custom skins. Players can copy custom skin (PNG image) files to the 'skins/' folder and then set them in the app to change the skin. 'defskin.png' is the default skin (size 200 x 400, this file can be deleted, and it will regenerate) that comes with 'Boxman.' Players can draw their own skins based on this format, noting that some elements use a transparent background. Additionally, 'Boxman' supports simplified skins (200 x 350, containing only two directions for the warehouse worker sprite) and ultra-simplified skins (200 x 150, containing only one wall and two directions for the warehouse worker sprite);").append('\n')
				 .append("  4. 'Boxman' supports level expansion, commonly referred to as 'import.' There are single-level imports, document imports, clipboard imports, and submissions via the level editor. In some import methods, solutions and batch imports can also be imported simultaneously;").append('\n')
				 .append("  5. To import level sets, first copy the level set files to the 'import/' folder, then select 'Import...' from the menu on the main interface;").append('\n')
				 .append("  6. For imported solutions obtained or optimized through YASS, 'Boxman' will automatically record '[Import]' or '[YASS]' in the 'Remarks' section of that solution;").append('\n')
				 .append("  7. 'Boxman' supports saving and exporting 'states' and 'solutions.' Exported files are placed in the 'export/' folder;").append('\n')
				 .append("  8. Level expansion and deletion only apply to levels expanded by the player, i.e., the 'Level Expansion' group;").append('\n')
				 .append("  9. In the Boxman editor, XSB import is supported, and Lurd import is also supported (i.e., reverse deriving the level XSB from the solution Lurd). Additionally, 'Boxman' supports automatic recognition of level images. Players can copy level screenshots to the 'level_images/' folder, then automatically recognize the level XSB through 'Image Recognition' in the level editor;").append('\n')
				 .append("  10. 'Boxman' supports level sizes ranging from 3 to 100 rows and 3 to 100 columns. Levels that exceed these dimensions or have mismatches between boxes and targets, or an invalid number of warehouse workers, are considered invalid and will not be accepted, indicated by a gray preview icon;").append('\n')
				 .append("  11. 'Boxman' supports the synthesis of GIF animations from solutions (or actions). For 'live skin' animations, you can place an image named 'banner.png' in the 'Boxman/' root folder (recommended size within '200 x 42', and it must be placed before starting Boxman to be effective) as a custom animation watermark. The default watermark is the domain name 'sokoban.cn.' The 'live skin' animation also supports manual box numbering and rulers for teaching purposes. The 'fixed skin' animation has the QQ number of the 'Boxman group' as the default watermark, with custom watermarks automatically taking the player's 'name' when submitting competition solutions.").append('\n')
				 .append('\n')
				 .append("====== Recent Updates ======").append('\n')
				 .append('\n')
				 .append("BoxMan9.99u~: 2024-03-27").append('\n')
				 .append("1. Added 'Double Tap Numbering' switch.").append('\n')
//  .append("1. Added color hints for levels with 'state' saved;").append('\n')
//  .append("2. Added batch modification function for level editing.").append('\n')
//  .append("2. Added 'Festival Solve' option for levels.").append('\n')
				 .append('\n')
				 .append("BoxMan9.99t: 2024-03-09").append('\n')
				 .append("1. Updated to support dual push interaction when exporting live XSB;").append('\n')
				 .append("2. Supported custom competition websites.").append('\n')
//  .append("2. Added 'Festival Solve' option for levels.").append('\n')
				 .append('\n')
				 .append("BoxMan9.99s: 2024-01-27").append('\n')
				 .append("1. Set the 'Show Time Prompt when Pushing Levels' switch to enabled by default;").append('\n')
				 .append("2. Fixed an issue where reverse actions couldn't be imported due to incorrect warehouse worker coordinates;").append('\n')
				 .append("3. Added 'Leave' option to the 'More' section of the level editor module;").append('\n')
				 .append("4. Resolved an issue where the 'More' button in the level pushing and editing interfaces could only be used once on new phones;").append('\n')
				 .append("5. Updated SDK to v33 for recompilation.").append('\n')
				 .append('\n')
				 .append("BoxMan9.99r: 2023-02-14").append('\n')
				 .append("1. Added a 'Reset to Default' button in the 'Switch Settings' section of the 'Push Box' interface;").append('\n')
				 .append("2. Fixed a misjudgment issue with 'Diagonal' deadlocks (gl).").append('\n')
				 .append('\n')
				 .append("BoxMan9.99q: 2023-02-08").append('\n')
				 .append("1. Added support for the fourth competition level.").append('\n')
				 .append('\n')
				 .append("BoxMan9.99n: 2022-03-30").append('\n')
				 .append("1. Fixed an issue where optimizing skin effects caused the right side of the warehouse worker's original position to display abnormally under reverse push state (Torchbearer);").append('\n')
				 .append("2. Added 'Exit' menu to the 'Push Box' interface.").append('\n')
				 .append('\n')
				 .append("BoxMan9.99m: 2022-03-11").append('\n')
				 .append("1. Fixed an issue where splitting skins early caused half-transparent issues in level images after viewing duplicate levels (Meditation Practice, Jumping Spirit).").append('\n')
				 .append('\n')
				 .append("BoxMan9.99k: 2022-03-09").append('\n')
				 .append("1. Optimized skin effects.").append('\n')
				 .append('\n')
				 .append("BoxMan9.99j: 2022-02-22").append('\n')
				 .append("1. Fixed a bug where importing a level from the clipboard in the level browsing interface caused a crash.").append('\n')
				 .append('\n')
				 .append("BoxMan9.99i: 2022-02-16").append('\n')
				 .append("1. Fixed a bug where the app crashed when the warehouse worker long-pressed the 'floor' during reverse push (Torchbearer).").append('\n')
				 .append('\n')
				 .append("BoxMan9.99h: 2021-05-25").append('\n')
				 .append("1. To enhance drawing effects, the wall top tile is shrunk inward by one pixel from all sides when drawing the wall top to avoid color interference around the wall top tile.").append('\n')
				 .append('\n')
				 .append("BoxMan9.99g: 2021-03-09").append('\n')
				 .append("1. Optimized the use of the clipboard when importing actions;").append('\n')
				 .append("2. Added a network lock reminder, restricting its use in non");
			 }
		 }

		 tv_help.setText(s.toString());
	 }
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
		 //返回键
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
