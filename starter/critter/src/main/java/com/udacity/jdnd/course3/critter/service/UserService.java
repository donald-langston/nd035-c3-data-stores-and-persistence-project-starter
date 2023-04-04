package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
@Transactional
public class UserService {
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    PetService petService;


    public Customer saveCustomer(Customer customer) { return customerRepository.save(customer); }

    public Customer updatePetsByCustomer(Long id) {
        Optional<Pet> optPet = petService.getPet(id);
        Pet foundPet = optPet.get();
        Customer foundCustomer = customerRepository.getOne(foundPet.getOwnerId());
        List<Pet> pets = petService.getPetsByOwnerId(foundCustomer.getId());
        foundCustomer.setPets(pets);
        pets.forEach(pet -> pet.setCustomer(foundCustomer));
        saveCustomer(foundCustomer);

        return foundCustomer;
    }

    public List<Customer> getCustomers() { return customerRepository.findAll(); }

    public Optional<Customer> getCustomerById(Long id) { return customerRepository.findById(id); }

    public Customer getCustomerByPet(Long petId) {
        Optional<Pet> optPet = petService.getPet(petId);
        Pet pet = optPet.get();
        //Customer customer = pet.getCustomer();
        Optional<Customer> optCustomer = getCustomerById(pet.getOwnerId());
        Customer customer = optCustomer.get();
        List<Pet> pets = petService.getPetsByOwnerId(customer.getId());
        customer.setPets(pets);
        pets.forEach(p -> p.setCustomer(customer));
        return saveCustomer(customer);

        //return customer;
    }

    public Employee saveEmployee(Employee employee) { return employeeRepository.save(employee); }

    public Employee getEmployee(Long id) { return employeeRepository.getOne(id); }

    public void setAvailability(Set<DayOfWeek> daysAvailable, Long employeeId) {
        Employee employee = employeeRepository.getOne(employeeId);
        employee.setDaysAvailable(daysAvailable);
        employeeRepository.save(employee);
    }

    public List<Employee> getEmployees() { return employeeRepository.findAll(); }

    public List<Employee> findEmployeesForService(EmployeeRequestDTO employeeRequest) {
        Locale locale = new Locale("EN");
        String day = getDayString(employeeRequest.getDate(), locale);
        List<Employee> employees = getEmployees();
        List<Employee> availableEmployees = new ArrayList<>();


        employees.forEach(employee -> {
           if(employee.getDaysAvailable().contains(DayOfWeek.valueOf(day.toUpperCase())) && employee.getSkills().containsAll(employeeRequest.getSkills())) {
                availableEmployees.add(employee);
           }
        });

        return availableEmployees;
    }

    private static String getDayString(LocalDate date, Locale locale) {
        DayOfWeek day = date.getDayOfWeek();
        return day.getDisplayName(TextStyle.FULL, locale);
    }
}
