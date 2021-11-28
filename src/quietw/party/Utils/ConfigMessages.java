package quietw.party.Utils;

import quietw.party.QParty;

import java.util.Objects;

public class ConfigMessages {

    public static String getMessage(String path) {
        return Objects.requireNonNull(QParty.getInstance().getConfig().getString(path)).replace("&", "§");
    }

}
