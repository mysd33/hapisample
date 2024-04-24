package com.example.hapisample.domain.utl;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

/**
 * Logのユーティリティクラス
 */
public final class LogUtils {
	private LogUtils() {
	}

	/**
	 * 処理時間をミリ秒単位にしてログに出力する
	 * 
	 * @param logger    Logger
	 * @param label     ログに出力する日本語ラベル
	 * @param startTime 開始時間(ns)
	 * @param endTime   終了時間(ns)
	 */
	public static void logElaspedTimeMillSecondUnit(Logger logger, String label, long startTime, long endTime) {
		logger.info("{}：{}ms", label, TimeUnit.NANOSECONDS.toMicros(endTime - startTime) / 1000d);
	}
}
