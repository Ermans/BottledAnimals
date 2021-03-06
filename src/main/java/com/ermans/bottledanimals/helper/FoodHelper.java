package com.ermans.bottledanimals.helper;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class FoodHelper {
    private static Map<Integer, Double> foodValues = new HashMap<Integer, Double>();

    public static void initItemValue() {
        for (Object o : GameData.getItemRegistry()) {
            Item item = (Item) o;

            if (item instanceof ItemFood && !item.getHasSubtypes()) {
                foodValues.put(generateKey(item), calculateFoodValue(new ItemStack(item, 1, 0)));
            }
        }
        foodValues.put(generateKey(Items.cake), 2D);
    }

    private static int generateKey(ItemStack stack) {
        Item item = stack.getItem();
        if (item.getHasSubtypes()) {
            return item.hashCode() + stack.getItemDamage();
        }
        return item.hashCode();
    }

    private static int generateKey(Item item) {
        return item.hashCode();
    }

    private static double calculateFoodValue(ItemStack itemStack) {
        if ((itemStack.getItem() instanceof ItemFood)) {

            ItemFood food = (ItemFood) itemStack.getItem();
            if (((food instanceof ItemFishFood)) &&
                    (ItemFishFood.FishType.func_150974_a(itemStack.getItemDamage()) == ItemFishFood.FishType.PUFFERFISH)) {
                return 0.01;
            }

            int potionID = getIntFromField(getFieldInClassUpTo("potionId", food.getClass(), Item.class), food);
            int potionDuration = getIntFromField(getFieldInClassUpTo("potionDuration", food.getClass(), Item.class), food);
            int potionAmplifier = getIntFromField(getFieldInClassUpTo("potionAmplifier", food.getClass(), Item.class), food);
            float potionEffectProbability = getFloatFromField(getFieldInClassUpTo("potionEffectProbability", food.getClass(), Item.class), food);

            double saturation = food.func_150906_h(itemStack);
            double heal = food.func_150905_g(itemStack);
            if ((potionID > 0) && (potionID < Potion.potionTypes.length)) {
                Potion potion = Potion.potionTypes[potionID];
                if (potion.isBadEffect()) {
                    if (potion.id == Potion.hunger.id) {
                        heal -= potionDuration * (potionAmplifier + 1) * 0.025;
                        heal *= (1 - potionEffectProbability);
                    } else if (potion.id == Potion.harm.id) {
                        heal -= (potionAmplifier + 1) * 6;
                    } else if (potion.id == Potion.poison.id) {
                        heal -= (potionAmplifier + 1) * potionDuration * 0.25;
                        saturation *= 0.5;
                    } else {
                        heal *= 0.5;
                        saturation *= 0.5;
                    }
                }
            }
            return Math.max(0.01, Math.floor(saturation * heal * 1000) / 1000);
        }
        return 0;
    }

    private static int getIntFromField(Field field, Object classObj) {
        int def = -1;
        if (field == null) {
            return def;
        }
        try {
            return field.getInt(classObj);
        } catch (IllegalAccessException ignored) {
        }
        return def;
    }

    private static float getFloatFromField(Field field, Object classObj) {
        float def = -1;
        if (field == null) {
            return def;
        }
        try {
            return field.getFloat(classObj);
        } catch (IllegalAccessException ignored) {
        }
        return def;
    }

    private static String getStringFromField(Field field, Object classObj) {
        if (field == null) {
            return null;
        }
        try {
            return (String) field.get(classObj);
        } catch (IllegalAccessException ignored) {
        }
        return null;
    }

    //Thanks to https://github.com/dancerjohn/LibEx for this method
    private static Field getFieldInClassUpTo(String field, Class fromClass, Class toClass) {
        Field resultField = null;
        try {
            resultField = fromClass.getDeclaredField(field);
        } catch (Exception ignored) {
        }
        if (resultField == null) {
            Class<?> parentClass = fromClass.getSuperclass();
            if ((parentClass != null) && ((toClass == null) || (!parentClass.equals(toClass)))) {
                resultField = getFieldInClassUpTo(field, parentClass, toClass);
            }
        } else {
            resultField.setAccessible(true);
        }
        return resultField;
    }

    public static double getFoodValue(ItemStack stack) {
        if (stack == null) {
            return 0;
        }

        int key = generateKey(stack);
        Double foodValue = foodValues.get(key);
        if (foodValue != null) return foodValue;

        if (stack.getHasSubtypes()) {
            foodValue = calculateFoodValue(stack);
            foodValues.put(key, foodValue);
        }
        return 0;
    }

    public static Map<Integer, Double> getFoodValues() {
        return foodValues;
    }

}
