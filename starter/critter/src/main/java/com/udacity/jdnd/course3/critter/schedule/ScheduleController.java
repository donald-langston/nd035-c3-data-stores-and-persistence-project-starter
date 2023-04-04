package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import com.udacity.jdnd.course3.critter.service.UserService;
import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import com.udacity.jdnd.course3.critter.user.Employee;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    UserService userService;

    @Autowired
    PetService petService;

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        //Schedule schedule = scheduleService.saveSchedule(convertScheduleDTOToEntity(scheduleDTO));
        Schedule schedule = scheduleService.saveSchedule(scheduleDTO);
        return convertScheduleToScheduleDTO(schedule);
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getSchedules();
        List<ScheduleDTO> schedulesDto = new ArrayList<>();
        schedules.forEach(schedule -> schedulesDto.add(convertScheduleToScheduleDTO(schedule)));
        return schedulesDto;
    }

    @GetMapping("/{scheduleId}")
    public ScheduleDTO getScheduleById(@PathVariable long scheduleId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return convertScheduleToScheduleDTO(schedule);
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        List<Schedule> schedules = scheduleService.getScheduleForPet(petId);
        List<ScheduleDTO> schedulesDto = new ArrayList<>();
        schedules.forEach(schedule -> schedulesDto.add(convertScheduleToScheduleDTO(schedule)));
        return schedulesDto;
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        List<Schedule> schedules = scheduleService.getScheduleForEmployee(employeeId);
        List<ScheduleDTO> schedulesDto = new ArrayList<>();
        schedules.forEach(schedule -> schedulesDto.add(convertScheduleToScheduleDTO(schedule)));
        return schedulesDto;
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        List<Schedule> schedules = scheduleService.getScheduleForCustomer(customerId);
        List<ScheduleDTO> schedulesDto = new ArrayList<>();
        schedules.forEach(schedule -> schedulesDto.add(convertScheduleToScheduleDTO(schedule)));
        return schedulesDto;
    }

    private ScheduleDTO convertScheduleToScheduleDTO(Schedule schedule){
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, scheduleDTO);

        if(schedule.getPets() != null) {
            List<Long> petIds = schedule.getPets().stream().map(pet -> pet.getId()).collect(Collectors.toList());
            scheduleDTO.setPetIds(petIds);
        }

        if(schedule.getEmployees() != null) {
            List<Long> employeeIds = schedule.getEmployees().stream().map(employee -> employee.getId()).collect(Collectors.toList());
            scheduleDTO.setEmployeeIds(employeeIds);
        }

        return scheduleDTO;
    }

    public Schedule convertScheduleDTOToEntity(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        List<Long> employeeIds = scheduleDTO.getEmployeeIds();
        List<Long> petIds = scheduleDTO.getPetIds();
        List<Employee> employees = new ArrayList<>();
        List<Pet> pets = new ArrayList<>();

        employeeIds.forEach(id -> {
            employees.add(userService.getEmployee(id));
        });

        petIds.forEach(id -> {
            Optional<Pet> optPet = petService.getPet(id);
            pets.add(optPet.get());
        });

        schedule.setEmployees(employees);
        schedule.setPets(pets);

        return schedule;
    }
}
