package vv.assignment.restful.Test;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.Adress;
import vv.assignment.restful.Customer;
import vv.assignment.restful.user.User;

import java.io.File;
import java.net.URI;
import java.security.KeyStore;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.*;




