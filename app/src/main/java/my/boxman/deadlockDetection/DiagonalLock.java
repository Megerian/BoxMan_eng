package my.boxman.deadlockDetection;

import my.boxman.myGameView;

// 这个类，用于检查“闭锁对角”死锁（移植于 JSoko）
public class DiagonalLock {

	final byte UP     = 0;
	final byte DOWN   = 1;
	final byte LEFT   = 2;
	final byte RIGHT  = 3;

	final private int NONE = -1;
	private int[] offset = { -100, 100, -1, 1 };      // 方便方向计算的数组，上、下、左、右

	private myGameView board;
	private char[][] level;                           // 地图指针
	private int nWidth, nHeight;                      // 地图的宽、高
	private int playerPosition;                       // 仓管员位置

	public DiagonalLock(final myGameView board) {
		this.board = board;
	}

	public boolean isDeadlock(int boxPosition) {
		level   = board.m_cArray;
		nHeight = level.length;
		nWidth  = level[0].length;
		offset[0] = -nWidth;
		offset[1] = nWidth;
		playerPosition = board.m_nRow * nWidth + board.m_nCol;  // 当仓管员位于对角线上时，不会死锁

		return isDiagonalDeadlock(boxPosition + offset[LEFT],  boxPosition) ||  // 与左邻组合后的情况
			   isDiagonalDeadlock(boxPosition + offset[RIGHT], boxPosition);    // 与右邻组合后的情况
	}

	private boolean isDiagonalDeadlock(int neighborPosition, int boxPosition) {

		int dx, dy;

		int goalOrWallSquareOnDiagonalPosition;  // 记录一个检查点的位置，当该点所在的行及之前的行，没有“剩下”非目标位的箱子时，记录该点，用于甄别下一行是否允许采用向里推的方式消除非目标位的箱子

		int current_Position;   // 当前检查点（对角线上的点）

		boolean isAllGoalsAndWallsSequence;  // 是否没有“剩下”非目标位的箱子

		// “传入”行是否为端点行（顶、底）
		boolean isTopBottomStarting = isWall_Box(neighborPosition);

		// 先向“左上”对角线方向检查
		int H_Direction = LEFT;
		int V_Direction = UP;

		// 传入点属于端点行时，需要检查 4 条对角线（上下各两条）；传入点属于中间行时，只需检查“上面”两个方向即可，因为检查会自动“折返”
		while(true) {
			dx = offset[H_Direction];
			dy = offset[V_Direction];

			goalOrWallSquareOnDiagonalPosition = NONE;  // 传入的是中间行时，做“折返”检查需要这个位置

			// 检查当前对角线（传入的是“中间行”时，还包含了“折返”的检查，从而构成完整的“对角线”）
			cur_Diagonal:
			while(true) {
				// 准备下一轮的检查点，并检查有没有“剩下”非目标位的箱子
				if (isTopBottomStarting) {  // “起点”是端点行
					if(dx == offset[RIGHT]) {
						current_Position = neighborPosition < boxPosition ? neighborPosition : boxPosition;
					} else {
						current_Position = neighborPosition > boxPosition ? neighborPosition : boxPosition;
					}
					isAllGoalsAndWallsSequence = isWall_Goal(current_Position) && isWall_Goal(current_Position + dx);
					current_Position += dx + dy;
				} else {                    // “起点”不是端点行，还会有一次“折返”
					if (goalOrWallSquareOnDiagonalPosition == NONE) {
						current_Position = neighborPosition;
						isAllGoalsAndWallsSequence = false;
					} else {
						current_Position = goalOrWallSquareOnDiagonalPosition;
						isAllGoalsAndWallsSequence = isWall_Goal(current_Position + dx);  // 相当于下面的邻点2：neighbor2_Position
						current_Position += dx + dy;
					}
				}

				// 检查点是界外，则结束此方向的检查
				if (isOuter(current_Position)) {
					break cur_Diagonal;
				}

				// 逐行检查
				while (true) {
					if (playerPosition == current_Position) {  // 若仓管员在对角线上，则不会死锁
						break cur_Diagonal;
					}

					// 如“右上”行进:
					//  adb     a = neighbor1_Position, d = current_Position, b = neighbor2_Position
					// % %
					// %%
					int neighbor1_Position = current_Position - dx;  // 邻点1
					int neighbor2_Position = current_Position + dx;  // 邻点2

					if (isPass(neighbor1_Position)) {  // 邻点1 是通道，不会构成“对角”死锁，结束此方向的检查
						break cur_Diagonal;
					}

					if (isAllGoalsAndWallsSequence) {  // 此前，没有“剩下”非目标位的箱子

						// 如“右上”行进的检查:
						//
						//   ?!    <---- '!' 当前检查点，'?' neighbor1_Position
						//  %.#    <---- '.' 上次检查点, '#' 当前检查点下面
						// % %
						// %%
						//
						// 若可以向内推，不会造成死锁。
						isAllGoalsAndWallsSequence = isWall_Goal(neighbor1_Position);

						if (isAllGoalsAndWallsSequence && isWall_Goal(current_Position)) {
							break cur_Diagonal;
						}
					}
					if (isPass(current_Position)) {  // 检查点是通道

						if(isPass(neighbor2_Position)) {  // neighbor2_Position 是通道，不会构成“对角”，结束此方向的检查
							break cur_Diagonal;
						}

						if (isGoal(current_Position)) {  // 检查点是目标点
							isAllGoalsAndWallsSequence = true;

							// 折返检查时，将从这个位置开始
							if (goalOrWallSquareOnDiagonalPosition == NONE) {
								goalOrWallSquareOnDiagonalPosition = current_Position;
							}
						}

						if (isAllGoalsAndWallsSequence) {
							isAllGoalsAndWallsSequence = isWall_Goal(neighbor2_Position);
						}

						// 遇到“双墙壁”端点:（还有两种变种的“双墙壁”端点）
						//       #      <- current_Position + dy
						//      *d#     <- d = current_Position; '#' = neighbor2_Position
						//     * *
						//     **
						if (isWall(neighbor2_Position) && isWall(current_Position + dy)) {     // 遇到“双墙壁”端点
							// 遇到“双墙壁”端点，就可以做“折返”检查的准备了
						} else {
							current_Position += dx + dy;  // 下一检查点
							continue;
						}

						if (isAllGoalsAndWallsSequence) {
							break cur_Diagonal;
						}

						goalOrWallSquareOnDiagonalPosition = current_Position + dx + dy;
					}

					// 遇到端点后，需要折返检查. 如:
					//  *$  <- blocked by box   *#  <- blocked by wall  #   <- blocked by
					// * *                     * *                     * #	<- two walls
					// **                      $*                      $*
					// 此时，已经遇到此方向上的端点
					if (!isTopBottomStarting && dy == offset[UP]) {  // 上行方向检查后，可能需要做反向检查
						// 反向检查时，需确保“可作为端点的检查点”是正确的
						if ((isWall_Goal(current_Position)) && goalOrWallSquareOnDiagonalPosition == NONE) {
							goalOrWallSquareOnDiagonalPosition = current_Position;
						}

						// 进入“折返”检查
						dx = -dx;
						dy = -dy;
						continue cur_Diagonal;
					} else {  // 两端遇到阻塞，向内推动会造成死锁
						return true;
					}
				} // End of while for 当前对角线的当前方向
			} // End of while for 当前对角线

			// 更换对角线: left/up, right/up, left/down, right/down.
			if (V_Direction == UP) {         // 上
				if(H_Direction == LEFT) {    // 左侧
					H_Direction = RIGHT;
				} else {                     // 右侧
					if (!isTopBottomStarting) {  // 传入的是中间行（非端点行），仅做“上方”的检查即可，因为会包含自动的“折返”检查
						return false;
					}
					// 此时，说明传入的行是端点行，则需要检查该行下面的对角线情况
					V_Direction   = DOWN;
					H_Direction = LEFT;
				}
			} else {                          // 下
				// 只有传入的是端点行时，才会执行到这里
				if (H_Direction == LEFT) {    // 左侧
					H_Direction = RIGHT;
				} else {                      // 右侧，此时，4 个方向都已经检查过了
					return false;
				}
			}
		}
	}

	//是否目标点
	public boolean isGoal(int pos) {
		int row = pos / nWidth, col = pos % nWidth;

		if (row < 0 || col < 0 || row >= nHeight || col >= nWidth) return false;

		return level[row][col] == '.' || level[row][col] == '*' || level[row][col] == '+';
	}

	//是否是通道
	public boolean isPass(int pos) {
		int row = pos / nWidth, col = pos % nWidth;

		if (row < 0 || col < 0 || row >= nHeight || col >= nWidth) return false;

		return level[row][col] == '-' || level[row][col] == '.' ||
				level[row][col] == '@' || level[row][col] == '+';
	}

	//是否是箱子、墙壁、墙外
	public boolean isWall_Box(int pos) {
		int row = pos / nWidth, col = pos % nWidth;

		return row < 0 || col < 0 || row >= nHeight || col >= nWidth ||
				level[row][col] == '#' || level[row][col] == '_' ||
				level[row][col] == '$' || level[row][col] == '*';
	}

	//是否是目标位、墙壁
	public boolean isWall_Goal(int pos) {
		int row = pos / nWidth, col = pos % nWidth;

		return row < 0 || col < 0 || row >= nHeight || col >= nWidth ||
				level[row][col] == '#' || level[row][col] == '_' ||
				level[row][col] == '.' || level[row][col] == '*' || level[row][col] == '+';
	}

	//是否墙
	public boolean isWall(int pos) {
		int row = pos / nWidth, col = pos % nWidth;

		return row < 0 || col < 0 || row >= nHeight || col >= nWidth ||
				board.m_Freeze[row][col] == 4 || board.m_Freeze[row][col] == 14 ||
				level[row][col] == '#' || level[row][col] == '_';
	}

	//是否墙外
	public boolean isOuter(int pos) {
		int row = pos / nWidth, col = pos % nWidth;

		return row < 0 || col < 0 || row >= nHeight || col >= nWidth || level[row][col] == '_';
	}
}