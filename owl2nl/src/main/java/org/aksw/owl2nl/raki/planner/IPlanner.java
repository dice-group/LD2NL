package org.aksw.owl2nl.raki.planner;

/**
 * @param <T>
 *
 * @author Rene Speck
 *
 * 
 */
public interface IPlanner<T> {

  /**
   * Initializes the planner and creates the results.
   */
  IPlanner<T> build();

  /**
   * Gets the results of this planner.
   *
   * @return
   */
  T results();
}
