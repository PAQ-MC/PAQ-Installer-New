package org.magetech.paq.installer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.sun.jna.Platform;
import org.apache.commons.io.FilenameUtils;
import org.magetech.paq.DirUtils;

import java.io.*;

public class Minecraft {
    public static final String MINECRAFT_LAUNCHER_PROFILES = "launcher_profiles.json";

    public static String getInstallDir() throws IOException {
        String appData = DirUtils.getBaseDataDir();
        if(Platform.isMac())
            return FilenameUtils.concat(appData, "minecraft");
        return FilenameUtils.concat(appData, ".minecraft");
    }

    public static String findForgeProfile() throws IOException {
        File configFile = getConfigFile();

        Gson gson = GsonUtil.make();
        JsonObject jobj;
        try (InputStream is = new FileInputStream(configFile);
            InputStreamReader isr = new InputStreamReader(is)) {
            jobj = gson.fromJson(isr, JsonElement.class).getAsJsonObject();
        }

        JsonObject profiles = jobj.getAsJsonObject("profiles");
        JsonObject forge = profiles.getAsJsonObject("Forge");
        if(forge == null)
            return null;

        return forge.getAsJsonPrimitive("lastVersionId").getAsString();
    }

    public static void updatePackLaunchProfile(String packName, String mcVersionId, String instDir) throws IOException {
        File configFile = getConfigFile();

        Gson gson = GsonUtil.make();
        JsonObject jobj;
        try (InputStream is = new FileInputStream(configFile);
            InputStreamReader isr = new InputStreamReader(is)) {
            jobj = gson.fromJson(isr, JsonElement.class).getAsJsonObject();
        }

        JsonObject profiles = jobj.getAsJsonObject("profiles");
        JsonObject profile = profiles.getAsJsonObject(packName);
        if(profile == null) {
            profile = new JsonObject();
            profiles.add(packName, profile);
            profile.addProperty("name", packName);
        }

        profile.addProperty("lastVersionId", mcVersionId);
        profile.addProperty("gameDir", instDir);

        try (OutputStream os = new FileOutputStream(configFile);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            JsonWriter writer = new JsonWriter(osw)) {

            gson.toJson(jobj, writer);
        }
    }

    private static File getConfigFile() throws IOException {
        String minecraftInstall = getInstallDir();
        if(!new File(minecraftInstall).exists()) {
            throw new IllegalStateException("Minecraft not found");
        }

        File configFile = new File(FilenameUtils.concat(minecraftInstall, MINECRAFT_LAUNCHER_PROFILES));
        if(!configFile.exists()) {
            throw new IllegalStateException("Minecraft " + MINECRAFT_LAUNCHER_PROFILES + " not found");
        }

        return configFile;
    }
}
