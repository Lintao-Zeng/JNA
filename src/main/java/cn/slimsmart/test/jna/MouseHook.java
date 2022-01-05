package cn.slimsmart.test.jna;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.MSG;

/**
 * 鼠标钩子
 */
public class MouseHook {
    //鼠标事件编码
    public static final int WM_MOUSEMOVE = 512;
    public static final int WM_LBUTTONDOWN = 513;
    public static final int WM_LBUTTONUP = 514;
    public static final int WM_RBUTTONDOWN = 516;
    public static final int WM_RBUTTONUP = 517;
    public static final int WM_MBUTTONDOWN = 519;
    public static final int WM_MBUTTONUP = 520;
    public User32 lib;
    private static HHOOK hhk;
    private MouseHookListener mouseHook;
    private HMODULE hMod;
    private boolean isWindows = false;

    public MouseHook() {
        isWindows = Platform.isWindows();
        if (isWindows) {
            lib = User32.INSTANCE;
            hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        }

    }
    //添加钩子监听
    public void addMouseHookListener(MouseHookListener mouseHook) {
        this.mouseHook = mouseHook;
        this.mouseHook.lib = lib;
    }
    //启动
    public void startWindowsHookEx() {
        if (isWindows) {
            lib.SetWindowsHookEx(WinUser.WH_MOUSE_LL, mouseHook, hMod, 0);
            int result;
            MSG msg = new MSG();
            while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
                if (result == -1) {
                    System.err.println("error in get message");
                    break;
                } else {
                    System.err.println("got message");
                    lib.TranslateMessage(msg);
                    lib.DispatchMessage(msg);
                }
            }
        }

    }
    //关闭
    public void stopWindowsHookEx() {
        if (isWindows) {
            lib.UnhookWindowsHookEx(hhk);
        }

    }

    public static void main(String[] args) {
        try {
            MouseHook mouseHook = new MouseHook();
            mouseHook.addMouseHookListener(new MouseHookListener() {
                //回调监听
                public LRESULT callback(int nCode, WPARAM wParam, MouseHookStruct lParam) {
                    if (nCode >= 0) {
                        switch (wParam.intValue()) {
                            case MouseHook.WM_MOUSEMOVE:
                                System.err.println("鼠标移动, x=" + lParam.pt.x + " y=" + lParam.pt.y);
                                break;
                            case MouseHook.WM_LBUTTONDOWN:
                                System.err.println("按下鼠标左键, x=" + lParam.pt.x + " y=" + lParam.pt.y);
                                break;
                            case MouseHook.WM_LBUTTONUP:
                                System.err.println("释放鼠标左键, x=" + lParam.pt.x + " y=" + lParam.pt.y);
                                break;
                            case MouseHook.WM_MBUTTONDOWN:
                                System.err.println("按下鼠标中键, x=" + lParam.pt.x + " y=" + lParam.pt.y);
                                break;
                            case MouseHook.WM_MBUTTONUP:
                                System.err.println("释放鼠标中键, x=" + lParam.pt.x + " y=" + lParam.pt.y);
                                break;
                            case MouseHook.WM_RBUTTONDOWN:
                                System.err.println("按下鼠标右键, x=" + lParam.pt.x + " y=" + lParam.pt.y);
                                break;
                            case MouseHook.WM_RBUTTONUP:
                                System.err.println("释放鼠标右键, x=" + lParam.pt.x + " y=" + lParam.pt.y);
                                break;
                        }
                    }
                    //将钩子信息传递到当前钩子链中的下一个子程，一个钩子程序可以调用这个函数之前或之后处理钩子信息
                    //hhk：当前钩子的句柄
                    //nCode ：钩子代码; 就是给下一个钩子要交待的，钩传递给当前Hook过程的代码。下一个钩子程序使用此代码，以确定如何处理钩的信息。
                    //wParam：要传递的参数; 由钩子类型决定是什么参数，此参数的含义取决于当前的钩链与钩的类型。
                    //lParam：Param的值传递给当前Hook过程。此参数的含义取决于当前的钩链与钩的类型。
                    return lib.CallNextHookEx(hhk, nCode, wParam, lParam.getPointer());
                }
            });
            mouseHook.startWindowsHookEx();

            Thread.sleep(20000);
            mouseHook.stopWindowsHookEx();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}