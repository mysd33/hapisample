package com.example.hapisample.domain.utl;

import org.slf4j.Logger;

/**
 * Logのユーティリティクラス
 */
public final class LogUtils {
	private LogUtils() {
	}
	
	/**
	 * 処理時間の結果をログに出力する
	 * @param log Logger
	 * @param label ログに出力する日本語ラベル
	 * @param startTime 開始時間
	 * @param endTime 終了時間
	 */
	public static void logElaspedTime(Logger log,  String label, long startTime, long endTime) {
		log.debug("{}：{}ms", label, endTime - startTime);
	}
}
