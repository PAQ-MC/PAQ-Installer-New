package org.magetech.paq.installer;

import org.magetech.paq.Launch;
import org.magetech.paq.NetUtils;
import org.magetech.paq.StreamUtils;
import org.magetech.paq.UrlUtils;
import org.magetech.paq.configuration.Property;
import org.magetech.paq.configuration.PropertyLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * Created by Aleksander on 13.12.13.
 */
public class ForgeInstaller {
    public static String install(String forgeUrl, boolean isServer) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String forgeFileName = UrlUtils.fileName(forgeUrl);

        String forgeVersionId = null;
        if(!isServer) {
            forgeVersionId = Minecraft.findForgeProfile();
            if(forgeVersionId != null) {
                // sample 1.6.4-Forge9.11.1.953
                String forgeVersion = forgeVersionId.replace("-Forge", "-");
                if(forgeFileName.contains(forgeVersion))
                    return forgeVersionId;
            }
        }

        File tmp = File.createTempFile("forge", ".jar");
        tmp.deleteOnExit();
        try(InputStream is = new URL(forgeUrl).openStream()) {
            StreamUtils.saveTo(is, tmp);
        }

        String[] args;
        if(isServer)
            args = new String[] { "installServer" };
        else
            args = new String[0];

        //Launch.jar(tmp, args);
        try { 
        Process p = Runtime.getRuntime().exec("java -jar " + "\"" + tmp.getAbsolutePath() + "\" ");
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		String line=null;
		
		while((line=input.readLine()) !=null ){
			System.out.println(line);
		}
	
	p.waitFor();
	if (p.exitValue() != 0) {
		System.out.println("forge error" + p.exitValue() );
	}
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        
        
        if(!isServer) {
            forgeVersionId = Minecraft.findForgeProfile();
        }

        return forgeVersionId;
    }
}
