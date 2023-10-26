package org.aksw.owl2nl.pipeline.planner;

/**
 * @param <T>
 *
 * @author Rene Speck
 *
 *
 */
public interface IPlanner<T> {

  /**
   * Executes the planner.
   */
  IPlanner<T> build();
}
