package org.magetech.paq.configuration;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.PointerType;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aleksander on 08.12.13.
 */
/*package*/ class WinNative {

    public static String getAppData() {
        HWND hwndOwner = null;
        int nFolder = Shell32.CSIDL_APPDATA;
        HANDLE hToken = null;
        int dwFlags = Shell32.SHGFP_TYPE_CURRENT;
        char[] pszPath = new char[Shell32.MAX_PATH];
        int hResult = Shell32.INSTANCE.SHGetFolderPath(hwndOwner, nFolder,
            hToken, dwFlags, pszPath);
        if (Shell32.S_OK == hResult) {
            String path = new String(pszPath);
            int len = path.indexOf('\0');
            path = path.substring(0, len);
            return path;
        } else {
            throw new IllegalStateException("Error: " + hResult);
        }
    }

    private static Map<String, Object> OPTIONS = new HashMap<String, Object>();
    static {
        OPTIONS.put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
        OPTIONS.put(Library.OPTION_FUNCTION_MAPPER,
            W32APIFunctionMapper.UNICODE);
    }

    static class HANDLE extends PointerType implements NativeMapped {
    }

    static class HWND extends HANDLE {
    }

    static interface Shell32 extends Library {

        public static final int MAX_PATH = 260;
        public static final int CSIDL_APPDATA = 0x001a;
        public static final int CSIDL_LOCAL_APPDATA = 0x001c;
        public static final int SHGFP_TYPE_CURRENT = 0;
        public static final int SHGFP_TYPE_DEFAULT = 1;
        public static final int S_OK = 0;

        static Shell32 INSTANCE = (Shell32) Native.loadLibrary("shell32",
            Shell32.class, OPTIONS);

        /**
         * see http://msdn.microsoft.com/en-us/library/bb762181(VS.85).aspx
         *
         * HRESULT SHGetFolderPath( HWND hwndOwner, int nFolder, HANDLE hToken,
         * DWORD dwFlags, LPTSTR pszPath);
         */
        public int SHGetFolderPath(HWND hwndOwner, int nFolder, HANDLE hToken,
                                   int dwFlags, char[] pszPath);

    }
}
