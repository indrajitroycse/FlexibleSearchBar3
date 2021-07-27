package com.yuqirong.searchbarview;

import com.yuqirong.flexiblesearchbarview.SearchBarView;
import ohos.aafwk.ability.delegation.AbilityDelegatorRegistry;
import ohos.agp.utils.Color;
import ohos.app.Context;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * ExampleOhosTest class
 */
public class ExampleOhosTest {
    /**
     * Context
     */
    final private transient Context context = AbilityDelegatorRegistry.getAbilityDelegator().getAppContext();
    /**
     * Custom View
     */
    final private transient SearchBarView searchBarView = new SearchBarView(context);

    /**
     * bundle test-case
     */
    @Test
    public void testBundleName() {
        final String actualBundleName = AbilityDelegatorRegistry.getArguments().getTestBundleName();
        assertEquals("testBundleName","com.yuqirong.searchbarview", actualBundleName);
    }

    /**
     * border color test-case
     */
    @Test
    public void testBorderColor() {
        searchBarView.setBorderColor(Color.BLACK);
        assertEquals("testBorderColor",Color.BLACK,searchBarView.getBorderColor());
    }

    /**
     * border color with Paint test-case
     */
    @Test
    public void testBoderColorPaint() {
        searchBarView.setBorderColorPaint(Color.BLACK);
        assertEquals("testBoderColorPaint",Color.BLACK,searchBarView.getBorderColorPaint());
    }
}