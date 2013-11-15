package com.suicune.pokeutils.tools;

import com.suicune.pokeutils.R;
import com.suicune.pokeutils.app.Natures;
import com.suicune.pokeutils.app.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class IVTools {
    public static String getIVsAsString(ArrayList<Integer> ivs) {
        if (ivs == null || ivs.size() < 1) {
            return null;
        } else if (ivs.size() == 1) {
            return "" + ivs.get(0);
        } else {
            return "" + ivs.get(0) + "-" + ivs.get(ivs.size() - 1);
        }
    }

	/**
	 * This method calculates all the possible IVs that match the given values.
	 * 
	 * @param stat
	 * @param nature
	 * @param currentStat
	 * @param currentEv
	 * @param currentLevel
	 * @param baseStat
	 * @return An ArrayList<Integer> with the calculated IVs
	 */
	public static ArrayList<Integer> calculatePossibleIVs(int stat, Natures.Nature nature,
			int currentStat, int currentEv, int currentLevel, int baseStat) {
        return calculatePossibleIVs(stat, nature, currentStat, currentEv, currentLevel, baseStat,
                null);
    }

    public static ArrayList<Integer> calculatePossibleIVs(int stat, Natures.Nature nature,
                                                          int currentStat, int currentEv,
                                                          int currentLevel, int baseStat,
                                                          List<Integer> previousValues){
		if (currentEv < 0 || currentEv > 255) {
			return null;
		}
        if(nature == null){
            nature = Natures.Nature.DOCILE;
        }
		ArrayList<Integer> ivList = new ArrayList<Integer>();
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        if(previousValues == null || previousValues.size() == 0){
            for(int iv = 0; iv <= 31; iv++){
                candidates.add(iv);
            }
        } else {
            candidates.addAll(previousValues);
        }

        for (Integer iv : candidates) {
    		if(stat == Pokemon.STAT_INDEX_HP) {
				if (currentStat == getHpValue(baseStat, currentEv, iv,
						currentLevel)) {
					ivList.add(iv);
				}
			} else {
                if (currentStat == getStatValue(baseStat, currentEv, iv,
                        currentLevel, Natures.getModifier(nature, stat))) {
                    ivList.add(iv);
                }
            }
        }
		return ivList;
	}

	public static int getHpValue(int baseStat, int currentEv, int iv,
			int currentLevel) {
		if(baseStat == 1){
			return 1;
		}
		Double result = Math
				.floor((((iv + (2 * baseStat) + (currentEv / 4) + 100) * currentLevel) / 100)
                        + 10);
		return result.intValue();
	}

	public static int getStatValue(int baseStat, int currentEv, int iv,
			int currentLevel, int currentNatureModifier) {
		if(baseStat == 1){
			return 1;
		}
		Double calculatedStat = (double) (2 * baseStat);
		double ev = currentEv / 4;
		calculatedStat += iv + ev;
		calculatedStat *= currentLevel;
		calculatedStat /= 100;
		calculatedStat += 5;
		calculatedStat *= currentNatureModifier;
		calculatedStat /= 100;
		calculatedStat = Math.floor(calculatedStat);
		return calculatedStat.intValue();
	}

    /**
     * Convenience method for calculation hidden power, based on the
     * stats.
     * @param hpIv
     * @param attIv
     * @param defIv
     * @param spAttIv
     * @param spDefIv
     * @param speedIv
     * @return
     */
    public static int getHiddenPowerType(int hpIv, int attIv, int defIv, int spAttIv, int spDefIv, int speedIv) {

        Double hiddenPowerTypeDouble = Math.floor(((1 * (hpIv % 2)) + (2 * (attIv % 2)) + (4 * (defIv % 2)) + (8 * (speedIv % 2)) + (16 * (spAttIv % 2)) + (32 * (spDefIv % 2))) * 15 / 63);
        switch (hiddenPowerTypeDouble.intValue()) {
            case 0:
                return R.string.type_fighting;
            case 1:
                return R.string.type_flying;
            case 2:
                return R.string.type_poison;
            case 3:
                return R.string.type_ground;
            case 4:
                return R.string.type_rock;
            case 5:
                return R.string.type_bug;
            case 6:
                return R.string.type_ghost;
            case 7:
                return R.string.type_steel;
            case 8:
                return R.string.type_fire;
            case 9:
                return R.string.type_water;
            case 10:
                return R.string.type_grass;
            case 11:
                return R.string.type_electric;
            case 12:
                return R.string.type_psychic;
            case 13:
                return R.string.type_ice;
            case 14:
                return R.string.type_dragon;
            case 15:
                return R.string.type_dark;
            default:
                return 0;
        }
    }
}
