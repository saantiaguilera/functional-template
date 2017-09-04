(ns parser_test
    (:require [clojure.test :refer :all]
      [parser :refer :all]
      [statement :refer :all])
    (:import (java.io FileNotFoundException)))

(deftest inflate-non-existent-file-returns-empty-vector
         (testing
           "Tests that inflating a non-existent file returns an empty vector (as if there was no lines."
           (is (thrown? FileNotFoundException (inflate-file "./res/asfd.txt")))))

(deftest inflate-existent-file-returns-vector-with-lines
         (testing
           "Tests that inflating an existent file returns a vector with the lines"
           (is (count (inflate-file "./res/test_fact_database.txt")) 7)
           (is (= (get (inflate-file "./res/test_fact_database.txt") 0)) "varon(juan).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 1)) "varon(pepe).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 2)) "mujer(maria).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 3)) "mujer(cecilia).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 4)) "padre(juan, pepe).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 5)) "padre(juan, maria).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 6)) "padre(pepe, cecilia).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 7)) nil)))

(deftest strip-facts-returns-them-correctly
         (testing
           "Tests that a wellformed vector of lines is stripped correctly into facts"
           (strip-file (inflate-file "./res/test_fact_database.txt"))
           (is (count facts) 7)                             ;; Check that there are 7 facts
           (is (= (:fact (get facts 0)) "varon"))           ;; Check that the first fact 'fact' is 'varon'
           (is (= (:params (get facts 0) ["juan"])))        ;; Check that the first fact 'params' is 'juan'
           (is (count rules) 0)                             ;; Check there are no rules
           (is (= (get rules 0) nil))))                     ;; Check that the first accessed element is indeed nil

(deftest strip-rules-returns-them-correctly
         (testing
           "Tests that a wellformed vector of lines is stripped correctly into rules"
           (strip-file (inflate-file "./res/test_rule_database.txt"))
           (is (count rules) 2)
           (is (= (:fact (:statement (get rules 0))) "hijo")) ;; hijo(X, Y) :- varon(X), padre(Y, X).
           (is (= (:params (:statement (get rules 0))) ["X" "Y"]))
           (is (= (:fact (nth (:conditions (get rules 0)) 0)) "varon"))
           (is (= (:params (nth (:conditions (get rules 0)) 0)) ["X"]))
           (is (= (:fact (nth (:conditions (get rules 0)) 1)) "padre"))
           (is (= (:params (nth (:conditions (get rules 0)) 1)) ["Y" "X"]))))

(deftest test-merge-results-as-or
         (testing
           "Tests combinations for merging a result list with an OR conj"
           (is (= (merge-result-list-as-or [0 0 0 0]) 0))
           (is (= (merge-result-list-as-or [1 1 1 0]) 0))
           (is (= (merge-result-list-as-or [1 1 1 1]) 1))
           (is (= (merge-result-list-as-or [1 1 1 0 1]) 0))))

(deftest test-merge-results-as-and
         (testing
           "Tests combinations for merging a result list with an AND conj"
           (is (= (merge-result-list-as-and [0 0 0 0]) 0))
           (is (= (merge-result-list-as-and [1 1 1 0]) 1))
           (is (= (merge-result-list-as-and [1 1 1 1]) 1))
           (is (= (merge-result-list-as-and [1 1 1 0 1]) 1))))