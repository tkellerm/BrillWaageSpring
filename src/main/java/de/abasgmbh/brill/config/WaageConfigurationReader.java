package de.abasgmbh.brill.config;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.management.BadAttributeValueExpException;
import java.io.*;

@Component
public class WaageConfigurationReader {

    private static Logger log = Logger.getLogger(WaageConfigurationReader.class.getName());


    public Waage read(final File cfgFile) throws IOException {
        log.debug("*** reading configuration file " + cfgFile.getAbsolutePath());

        if (cfgFile.exists() && cfgFile.isFile() && cfgFile.canRead()) {

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cfgFile)))) {
                String line;
                String lineWithoutWhitespace;
                // tmp
                String actualBlock = null;
                // config blocks
                boolean inBlock = false;
                boolean edpBlock = false;
                boolean waageBlock = false;
                boolean grenzenBlock = false;
                boolean newWaage = false;

                String waageName = "";
                String waageIpadress = "";
                int waagePort = 0;
                Integer waageLeuchtdauer = 0;
                Integer waageTextdauer = 0;
                Boolean waageAktive = false;
                Double waageRotGrenze = new Double(0);
                Double waageGelbGrenze = new Double(0);


                //
                while ((line = br.readLine()) != null) {
                    lineWithoutWhitespace = line.replaceAll("\\s+", "").trim();
                    if (lineWithoutWhitespace.length() == 0) {
                        continue;
                    }
                    if (lineWithoutWhitespace.startsWith("#")) {
                        continue;
                    }
                    /* ********
                     * [] BLOCKS
                     * ********
                     */
                    if (lineWithoutWhitespace.matches("^\\[.+\\]$")) {
                        actualBlock = lineWithoutWhitespace.replaceAll("\\[|\\]", "");
                        // reset


                        edpBlock = false;
                        waageBlock = false;
                        grenzenBlock = false;
                        inBlock = false;

//                        String test = lineWithoutWhitespace;
//                        Boolean testb = lineWithoutWhitespace.equalsIgnoreCase(test);
                        if (lineWithoutWhitespace.equalsIgnoreCase("[edpparameter]")) {
                            // [EDPPARAMETER]
                            edpBlock = true;
                            waageBlock = false;
                            grenzenBlock = false;
                            inBlock = true;
                        } else if (lineWithoutWhitespace.equalsIgnoreCase("[grenzen]")) {
                            // [GRENZEN]
                            edpBlock = false;
                            waageBlock = false;
                            grenzenBlock = true;
                            inBlock = true;
                            newWaage = false;
                        } else if (lineWithoutWhitespace.equalsIgnoreCase("[waage]")) {
                            // [WAAGE]
                            edpBlock = false;
                            waageBlock = true;
                            grenzenBlock = false;
                            inBlock = true;
                            newWaage = true;

                        }
                        continue;
                    }
                    /* ***
                     * content (key=value)
                     * ***
                     */
                    if (inBlock) {
                        String key = line.substring(0, line.indexOf('=') - 1).trim();
                        String value = line.substring(line.indexOf('=') + 1).trim();
                        // content [ EDPPARAMETER ]
//                        if (edpBlock) {
//                            if (key.equalsIgnoreCase("server")) {
//                                log.debug("server: " + value);
//                                configuration.setEdpServer(value);
//                            } else if (key.equalsIgnoreCase("port")) {
//                                log.debug("port: " + value);
//                                Integer integerValue = new Integer(value);
//                                configuration.setPort(integerValue);
//                            }else if (key.equalsIgnoreCase("mandant")) {
//                            	log.debug("mandant: " + value);
//                                configuration.setMandant(value);
//							}else if (key.equalsIgnoreCase("passwort")) {
//								log.debug("passwort: " + value);
//                                configuration.setPassword(value);
//							} 
//						}
                        // content [ GRENZEN ]
//                        if (grenzenBlock) {
//                            if (key.equalsIgnoreCase("rot")) {
//                                log.debug("rot: " + value);
//                                Double rotGrenze = new Double(value);
//                                configuration.setRotGrenze(rotGrenze);
//                            } else if (key.equalsIgnoreCase("gelb")) {
//                                log.debug("gelb: " + value);
//                                Double gelbGrenze = new Double(value);
//                                configuration.setGelbGrenze(gelbGrenze);
//                            } 
//						}
                        // content [ Waage ]
                        if (waageBlock) {
                            if (newWaage) {
//                            	Prüfen,ob schon ein Waagendatensatz existiert

                                if (!waageName.isEmpty() & !waageIpadress.isEmpty() & waagePort != 0) {
//                            			neue Waage anlegen 
                                    Waage waage = new Waage(waageName, waageIpadress, waagePort, waageLeuchtdauer, waageTextdauer, waageAktive, waageGelbGrenze , waageRotGrenze );
                                    return waage;
                                }
                            }

                            newWaage = false;
                            if (key.equalsIgnoreCase("name")) {
                                log.debug("name: " + value);
                                waageName = value;
                            } else if (key.equalsIgnoreCase("ipadresse")) {
                                log.debug("ipadresse: " + value);
                                waageIpadress = value;
                            } else if (key.equalsIgnoreCase("port")) {
                                log.debug("port: " + value);
                                waagePort = new Integer(value);
                            } else if (key.equalsIgnoreCase("leuchtdauer")) {
                                log.debug("leuchtdauer: " + value);
                                waageLeuchtdauer = new Integer(value);
                            } else if (key.equalsIgnoreCase("textdauer")) {
                                log.debug("textdauer: " + value);
                                waageTextdauer = new Integer(value);
                            } else if (key.equalsIgnoreCase("aktiv")) {
                                log.debug("aktiv: " + value);
                                waageAktive = checkValue2Boolean(value);
                            }else if (key.equalsIgnoreCase("rotgrenze")) {
                            	  log.debug("rotGrenze: " + value);
                                  waageRotGrenze = new Double(value);     
                            } else if (key.equalsIgnoreCase("gelbgrenze")) {
                                  log.debug("gelbGrenze: " + value);
                                  waageGelbGrenze = new Double(value);
                              } 
							}

                        }

                    

                }

//                letzte Waage dranhängen

                if (!waageName.isEmpty() & !waageIpadress.isEmpty() & waagePort != 0) {
                    //        			neue Waage anlegen und an die configuration hängen
                    Waage waage = new Waage(waageName, waageIpadress, waagePort, waageLeuchtdauer, waageTextdauer, waageAktive, waageGelbGrenze , waageRotGrenze);
                    return waage;
                } 
            } catch (NumberFormatException e) {
                log.error("Der Wert PORT konnte nicht in einen Integer Wert gewandelt werden", e);

            } catch (BadAttributeValueExpException e) {
                log.error("Die Parameter für die Waage sind fehlerhaft", e);
            }

        }
        log.debug("*** read configuration file " + cfgFile.getAbsolutePath());

        throw new IOException("Es wurde keine Waage in dem Config-File " + cfgFile.getAbsolutePath() + " gefunden");

    }


    private Boolean checkValue2Boolean(String value) throws BadAttributeValueExpException {
        Boolean bool;
        if (value.equals("1")) {
            bool = true;
        } else if (value.equals("0")) {
            bool = false;
        } else if (value.equalsIgnoreCase("ja")) {
            bool = true;
        } else if (value.equalsIgnoreCase("nein")) {
            bool = false;
        } else if (value.equalsIgnoreCase("true")) {
            bool = true;
        } else if (value.equalsIgnoreCase("false")) {
            bool = false;
        } else {
            throw new BadAttributeValueExpException("Für Aktiv bitte nur 1,0, ja, nein, true, false in dem Konfig-File angeben!/n Nicht " + value);
        }

        return bool;
    }


}
