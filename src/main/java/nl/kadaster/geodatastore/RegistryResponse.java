package nl.kadaster.geodatastore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PeltB on 22-1-2016.
 */
public class RegistryResponse {
    private List<RegistryItem> response;

    public RegistryItem[] getResponse() {
        int len = 0;
        RegistryItem[] denominator = null;

        if (response != null) {
            len = response.size();

            denominator = new RegistryItem[len];
            for(int i = 0; i<len; i++) {
                denominator[i] = response.get(i);
            }
        }
        return denominator;
    }

    public void setResponse(final RegistryItem[] response) {
        List<RegistryItem> denominators = new ArrayList<>();

        for(RegistryItem denom: response) {
            denominators.add(denom);
        }

        this.response = denominators;
    }
}
