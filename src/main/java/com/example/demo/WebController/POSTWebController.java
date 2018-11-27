package com.example.demo.WebController;

import com.example.demo.Domini.*;
import com.example.demo.FiltreIndex.Filtre;
import com.example.demo.LogIn.LogIn;
import com.example.demo.UploadImage.FileWeb;
import com.example.demo.UseCases.ReservaUseCases;
import com.example.demo.UseCases.RestaurantUseCases;
import com.example.demo.UseCases.UsuariUseCases;
import com.example.demo.DisponibilitatReserva.ValidarReserva;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


@Controller
public class POSTWebController {


    private UsuariUseCases usuUseCases;
    private RestaurantUseCases restUseCases;
    private ReservaUseCases rsvUseCases;


        public POSTWebController(UsuariUseCases usuUseCases, RestaurantUseCases rest,ReservaUseCases rsvUseCases) {
            this.usuUseCases = usuUseCases;
            this.restUseCases = rest;
            this.rsvUseCases=rsvUseCases;
        }


        @PostMapping("/newUsu")
        public String createClassroom(@Valid @ModelAttribute("usr") Usuari usr, Errors errors, Model model, RedirectAttributes redirectAttributes) {


            if (errors.hasErrors()) {
                model.addAttribute("usr", usr);

                return "newUsuari";
            }

            model.addAttribute("name", usr.getUserName());

            usuUseCases.insert(usr);

            redirectAttributes.addAttribute("name", usr.getUserName());

            return "redirect:/showUser/{name}";
        }


    @PostMapping("/newResv")
    public String createReservation(@Valid @ModelAttribute("rsv") Reserva rsv,@RequestParam("paramName") String nom,Errors errors, Model model, RedirectAttributes redirectAttributes) {

        if (errors.hasErrors()) {
            model.addAttribute("rsv", rsv);

            return "newReserva";
        }

        rsv.setRestaurant(nom);
        ValidarReserva var = new ValidarReserva(rsvUseCases, restUseCases);
        if(var.dataValida(rsv.getData_reserva())) {
            if (var.suficientCapacitat(rsv)) {
                rsvUseCases.insert(rsv);
                redirectAttributes.addAttribute("id_reserva", rsv.getId_reserva());
                return "redirect:/showRsv/{id_reserva}";
            }
            return "showNOreserva";
        }
            return "ReservaNOvalida";
    }


    @RequestMapping(value="/newRest", method=RequestMethod.POST, consumes = "multipart/form-data")
    public String handleFileUpload(@Valid @ModelAttribute("rest") Restaurant rest,@RequestParam("fichero") MultipartFile file, Errors errors, Model model, RedirectAttributes redirectAttributes) {

        if (errors.hasErrors()) {
            model.addAttribute("rest", rest);
            return "newRest";
        }


        model.addAttribute("name", rest.getNomRestaurant());

        FileWeb.handleFileUpload(file);
        rest.setFoto("/"+file.getOriginalFilename());
        restUseCases.insert(rest);
        redirectAttributes.addAttribute("name", rest.getNomRestaurant());
        return "redirect:/showRest/{name}";
    }


    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("usr") Usuari usr, Errors errors, Model model, RedirectAttributes redirectAttributes) {

        LogIn usuValidar= new LogIn(usr.getUserName(),usr.getPassword());

        if(usuUseCases.validateUser(usuValidar)==null){
            return "loginNOValidated";
        }else{
            LogIn login= new LogIn(usr.getUserName(),usr.getPassword());
            return "loginValidated";
        }
    }

    @RequestMapping(value="/busqueda",method = RequestMethod.POST)
    public String busqueda(@Valid @ModelAttribute("p") Filtre p, Errors errors, Model model, RedirectAttributes redirectAttributes) {

       switch(p.getOpcio()){

           case "Puntuacio":
               try{
                   double valor=Double.parseDouble(p.getValor());
                   redirectAttributes.addAttribute("valor", valor);
                   return "redirect:/puntuacio/{valor}";
               }catch(NumberFormatException ex){
                    return "redirect:/";
               }

           case "Ciutat":
               redirectAttributes.addAttribute("valor", p.getValor());
               return "redirect:/ciutat/{valor}";

           case "Nom":
               redirectAttributes.addAttribute("valor", p.getValor());
               return "redirect:/nom/{valor}";
       }
        return "redirect:/";
    }


    @PostMapping("puntuacio/{nom}")
    public String puntuacio(@PathVariable String nom, @RequestParam(value="tentacles", required=true) int param1,Model model,RedirectAttributes redirectAttributes) {

        this.restUseCases.puntuar(nom,param1);
        redirectAttributes.addAttribute("name",nom);
        return "redirect:/showRest/{name}";
    }

}


