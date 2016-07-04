/**
 * creation date: Jul 04, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.abasgmbh.brill.registration;

import de.abasgmbh.brill.config.Waage;
import de.abasgmbh.brill.config.WaageConfigurationReader;
import de.abasgmbh.brill.controller.WaagenController;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class WaageRegistrationService {

    Logger log = Logger.getLogger(WaageRegistrationService.class);

    @Autowired
    private WaageConfigurationReader configReader;

    @Autowired
    private Provider<WaagenController> waagenControllerProvider;

    @Value("${waage.config.dir}")
    private String configDir;

    private Map<String, Waage>waagenConfigs = new HashMap<>();
    private Set<String> registrierteWaagen = Collections.synchronizedSet(new HashSet<>());

    @Scheduled(initialDelay = 1, fixedDelay = 5000)
    private void checkConfigDiractory() {
        log.info("check config directory: " + this.configDir);
        try {
            Files.walk(Paths.get(configDir)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    //Todo: pruefen ob der name passt
                    performFilePath(filePath);
                }
            });
        } catch (Exception e) {
            log.error("Kann nicht aus dem Konfigurationsverzeichnus lesen.", e);
        }

    }

    private void performFilePath(Path filePath) {
        try {
            if (!this.waagenConfigs.containsKey(filePath.toString())) {
                Waage waageCfg = this.configReader.read(filePath.toFile());
                this.waagenConfigs.put(filePath.toString(), waageCfg);
            }
            Waage waageCfg = this.waagenConfigs.get(filePath.toString());
            if (!this.registrierteWaagen.contains(waageCfg.getName())) {
                startWaageConnection(waageCfg);
            }
        } catch (Exception e) {
            log.error("Kann nicht aus dem Konfigurationsverzeichnus lesen.", e);
        }
    }

    private void startWaageConnection(Waage waageCfg) {
    	if (waageCfg.isActive()) {
            log.info("start connection to waage: " + waageCfg.getName());
            WaagenController wctrl = this.waagenControllerProvider.get();
            wctrl.start(waageCfg);
        }
    }

    public void register(String waageName) {
        this.registrierteWaagen.add(waageName);
    }

    public void unRegister(String waageName) {
        this.registrierteWaagen.remove(waageName);
    }
}
