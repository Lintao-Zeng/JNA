package cn.slimsmart.test.jna;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.HOOKPROC;

/**
 * 定义鼠标钩子，及事件监听回调
 */
public abstract class MouseHookListener implements HOOKPROC {
    public User32 lib = null; //window应用程序接口
    public HHOOK hhk; //钩子的句柄
    //回调
    //返回这个值链中的下一个钩子程序，返回值的含义取决于钩型
    public abstract LRESULT callback(int nCode, WPARAM wParam, MouseHookStruct lParam);
}