package primal.bukkit.inventory;

import org.bukkit.inventory.ItemStack;

import primal.bukkit.world.MaterialBlock;
import primal.lang.collection.GList;

public interface Element
{
	MaterialBlock getMaterial();

	Element setMaterial(MaterialBlock b);

	boolean isEnchanted();

	Element setEnchanted(boolean enchanted);

	String getId();

	String getName();

	Element setProgress(double progress);

	double getProgress();

	short getEffectiveDurability();

	Element setCount(int c);

	int getCount();

	ItemStack computeItemStack();

	Element setBackground(boolean bg);

	boolean isBackgrond();

	Element setName(String name);

	Element addLore(String loreLine);

	GList<String> getLore();

	Element call(ElementEvent event, Element context);

	Element onLeftClick(Callback<Element> clicked);

	Element onRightClick(Callback<Element> clicked);

	Element onShiftLeftClick(Callback<Element> clicked);

	Element onShiftRightClick(Callback<Element> clicked);

	Element onDraggedInto(Callback<Element> into);

	Element onOtherDraggedInto(Callback<Element> other);
}
