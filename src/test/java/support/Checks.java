package support;

public class Checks {

    public boolean nameInServiceList(String name, RegistryServicesResponse myObjects) {

        boolean found = false;
        RegistryService[] services = myObjects.getRegistries();
        int len = services.length;
        int i = 0;

        while ((!found) && (i < len)) {
            found = name.equals(services[i].getName());
            i++;
        }

        return found;
    }

}
