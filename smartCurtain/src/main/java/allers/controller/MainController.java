package allers.controller;


import allers.service.RaspberryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/")
@RestController
public class MainController {

    @Autowired
    RaspberryService service;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value = "/open", method = RequestMethod.GET)
    @ApiOperation("Opens the Curtain")
    public void openCurtain(){
        System.out.println("Curtain wird geoeffnet");
        service.setState(RaspberryService.open);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value = "/close", method = RequestMethod.GET)
    public void closeCurtain(){
        System.out.println("Curtain wird geclosed");
        service.setState(RaspberryService.close);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value = "/getState", method = RequestMethod.GET)
    public void getState(){
        service.setState(RaspberryService.neutral);
    }
}
