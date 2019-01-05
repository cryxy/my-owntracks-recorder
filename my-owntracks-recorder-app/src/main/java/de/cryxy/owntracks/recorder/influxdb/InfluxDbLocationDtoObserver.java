package de.cryxy.owntracks.recorder.influxdb;

import javax.enterprise.event.ObservesAsync;

import de.cryxy.owntracks.commons.dtos.LocationDto;

public class InfluxDbLocationDtoObserver {
	
    public void observeEvent(@ObservesAsync LocationDto event){

    System.out.println("###" + event);

    }

}
