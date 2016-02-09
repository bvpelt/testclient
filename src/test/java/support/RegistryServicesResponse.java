package support;

import java.util.ArrayList;
import java.util.List;

public class RegistryServicesResponse {
    // private RegistryService[] registryServices = null;
    private List<RegistryService> registries;

    public RegistryServicesResponse() {

    }

    public RegistryService[] getRegistries() {

        int len = 0;

        RegistryService[] regs = null;
        if (registries != null) {
            len = registries.size();

            regs = new RegistryService[len];
            for (int i = 0; i < len; i++) {
                regs[i] = registries.get(i);
            }
        }

        return regs;
    }

    public void setRegistries(final RegistryService[] registries) {

        List<RegistryService> regs = new ArrayList<>();

        for (RegistryService r : registries) {
            regs.add(r);
        }

        this.registries = regs;
    }

}
