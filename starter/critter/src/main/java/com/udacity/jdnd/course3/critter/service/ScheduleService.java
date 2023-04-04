package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.schedule.Schedule;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import com.udacity.jdnd.course3.critter.schedule.ScheduleRepository;
import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.Employee;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleService {
    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    UserService userService;

    @Autowired
    PetService petService;

    public Schedule saveSchedule(ScheduleDTO scheduleDTO) {
        Schedule savedSchedule = scheduleRepository.save(convertScheduleDTOToEntity(scheduleDTO));
        return savedSchedule;
    }

    public List<Schedule> getSchedules() { return scheduleRepository.findAll(); }

    public Schedule getScheduleById(Long scheduleId) { return scheduleRepository.getOne(scheduleId); }

    public List<Schedule> getScheduleForEmployee(Long employeeId) {
        List<Schedule> schedules = getSchedules();
        List<Schedule> selectedSchedules = new ArrayList<>();
        schedules.forEach(schedule -> {
            schedule.getEmployees().forEach(employee -> {
                if(employee.getId() == employeeId) {
                    selectedSchedules.add(schedule);
                }
            });
        });
        return selectedSchedules;
    }

    public List<Schedule> getScheduleForPet(Long petId) {
        List<Schedule> schedules = getSchedules();
        List<Schedule> selectedSchedules = new ArrayList<>();
        schedules.forEach(schedule -> {
            schedule.getPets().forEach(pet -> {
                if(pet.getId() == petId) {
                    selectedSchedules.add(schedule);
                }
            });
        });
        return selectedSchedules;
    }

    public List<Schedule> getScheduleForCustomer(Long customerId) {
        Optional<Customer> customerOpt = userService.getCustomerById(customerId);
        Customer customer = customerOpt.get();

        List<Schedule> allSchedules = getSchedules();
        List<Schedule> schedules = allSchedules.stream().filter(schedule -> {

          return schedule.getPets().stream().map(pet -> {return pet.getOwnerId(); })
                   .collect(Collectors.toList())
                   .contains(customerId);
        }).collect(Collectors.toList());

        return schedules;
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

        pets.forEach(pet -> pet.setSchedule(schedule));
        employees.forEach(employee -> employee.setSchedule(schedule));

        return schedule;
    }
}
