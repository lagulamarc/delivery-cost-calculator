package com.mynt.exam.deliverycostcalculator.util;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// Enum class for rules where priority is assigned
// Can be moved to an Entity class for more flexibility
@Getter
@RequiredArgsConstructor
public enum RulePriority {

	
	REJECT(1, "REJECT", "REJECT"), 
	HEAVY_PARCEL(2, "HEAVY", "CALCULATE"), 
	SMALL_PARCEL(3, "SMALL", "CALCULATE"), 
	MEDIUM_PARCEL(4, "MEDIUM", "CALCULATE"),
	LARGE_PARCEL(5, "LARGE", "CALCULATE");

	private final int priority;
	private final String remark;
	private final String action;

	private static final Map<Integer, RulePriority> ruleMap = Collections
			.unmodifiableMap(initializeOrderedRulePriority());
	
	public static  Map<Integer, RulePriority> getRules() {
		return ruleMap;
	}

	private static Map<Integer, RulePriority> initializeOrderedRulePriority() {
		Map<Integer, RulePriority> ruleMap = new TreeMap<>();
		for (RulePriority rule : RulePriority.values()) {
			ruleMap.put(rule.getPriority(), rule);
		}
		return ruleMap;
	}

}
