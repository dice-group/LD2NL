/*-
 * #%L
 * OWL2NL
 * %%
 * Copyright (C) 2015 - 2021 Data and Web Science Research Group (DICE)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.aksw.owl2nl.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.owl2nl.converter.visitors.OWLClassExpressionToNLGElement;
import org.aksw.owl2nl.data.IInput;
import org.aksw.owl2nl.data.OWL2NLInput;
import org.aksw.owl2nl.util.grammar.Words;
import org.aksw.owl2nl.util.nlg.Phrases;
import org.aksw.triple2nl.property.PropertyVerbalization;
import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLPropertyAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

import simplenlg.features.Feature;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.AdvPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * Converts OWL logical axioms into natural language.
 *
 * @author Lorenz Buehmann
 * @author Rene Speck
 */
public class OWLAxiomConverter extends AConverter implements OWLAxiomVisitor {

  private final OWLClassExpressionConverter ceConverter;
  private final OWLPropertyExpressionConverter peConverter;

  private final List<NLGElement> clauses = new ArrayList<>();

  /**
   * OWLAxiomConverter class constructor.
   *
   * @param input
   */
  public OWLAxiomConverter(final IInput input) {
    super(input);

    ceConverter = new OWLClassExpressionConverter(input);
    peConverter = new OWLPropertyExpressionConverter(input);
  }

  /**
   * OWLAxiomConverter class constructor.
   *
   * @param lexicon
   */
  public OWLAxiomConverter(final Lexicon lexicon) {
    this(new OWL2NLInput().setLexicon(lexicon));
  }

  /**
   * OWLAxiomConverter class constructor.
   *
   */
  public OWLAxiomConverter() {
    this(Lexicon.getDefaultLexicon());
  }

  protected void optimize(final List<NLGElement> clauses) {
    // TODO: merge objects and subjects

    /**
     * <code>
     for (final NLGElement clause : clauses) {
       if (clause.getCategory().toString().equals("CLAUSE")) {
         final NLGElement subject = ((SPhraseSpec) clause).getSubject();
         final NLGElement object = ((SPhraseSpec) clause).getObject();

         LOG.trace("s: {} ", realiser.realise(subject).toString());
         LOG.trace("o: {} ", realiser.realise(object).toString());
       }
     }
     </code>
     */
  }

  /**
   * Converts the OWL axiom into natural language. Only logical axioms are supported, i.e.
   * declaration axioms and annotation axioms are not converted and <code>null</code> will be
   * returned instead.
   *
   * @param axiom the OWL axiom
   * @return the natural language expression
   */
  public String convert(final OWLAxiom axiom) {
    LOG.debug("============================================");

    clauses.clear();

    if (!axiom.isLogicalAxiom()) {
      LOG.debug("Non logical axioms aren't supported yet.");
      return null;
    } else {
      LOG.debug(axiom);
      axiom.accept(this);
      optimize(clauses);
      final StringBuilder sb = new StringBuilder();
      for (final NLGElement clause : realiser.realise(clauses)) {
        sb.append(StringUtils.capitalize(clause.toString())).append(". ");
      }
      return sb.toString();
    }
  }

  // #########################################################
  // ################# OWLAxiomVisitor #######################
  // #########################################################

  /**
   * A subclass axiom SubClassOf(CE1 CE2) states that the class expression CE1 is a subclass of the
   * class expression CE2<br>
   * <br>
   * Example:<br>
   * <br>
   * class expression: SubClassOf(a:Child a:Person)<br>
   * <br>
   * converted: Each child is a person.
   */
  @Override
  public void visit(final OWLSubClassOfAxiom axiom) {
    LOG.debug("Converting SubClassOf axiom: {}", axiom);
    addClause(//
        ceConverter.asNLGElement(axiom.getSubClass(), true), //
        Phrases.getBe(nlgFactory), //
        ceConverter.asNLGElement(axiom.getSuperClass())//
    ).getObject().setFeature(Feature.COMPLEMENTISER, null);
  }

  /**
   * An equivalent classes axiom EquivalentClasses(CE1 ... CEn) states that all of the class
   * expressions CEi, 1 ≤ i ≤ n, are semantically equivalent to each other.<br>
   * <br>
   * Example:<br>
   * <br>
   * class expression: EquivalentClasses(a:Boy ObjectIntersectionOf(a:Child a:Man))<br>
   * <br>
   * converted: A boy is a male child.<br>
   * <br>
   * We rewrite EquivalentClasses(CE1 CE2) as SubClassOf(CE1 CE2) and SubClassOf(CE2 CE1)
   */
  @Override
  public void visit(final OWLEquivalentClassesAxiom axiom) {
    final List<OWLClassExpression> classExpressions = axiom.getClassExpressionsAsList();
    LOG.debug("Converting EquivalentClasses axiom: {}, {}", axiom, classExpressions);

    for (int i = 0; i < classExpressions.size(); i++) {
      for (int j = i + 1; j < classExpressions.size(); j++) {
        df.getOWLSubClassOfAxiom(//
            classExpressions.get(i), classExpressions.get(j)//
        ).accept(this);
      }
    }
  }

  /**
   * A disjoint classes axiom DisjointClasses(CE1 ... CEn) states that all of the class expressions
   * CEi, 1 ≤ i ≤ n, are pairwise disjoint; that is, no individual can be at the same time an
   * instance of both CEi and CEj for i ≠ j.<br>
   * <br>
   * Example:<br>
   * <br>
   * class expression: DisjointClasses(a:Boy a:Girl)<br>
   * <br>
   * converted: Nothing can be both a boy and a girl.<br>
   * <br>
   * We rewrite DisjointClasses(CE1 ... CEn) as SubClassOf(CEi, ObjectComplementOf(CEj)) for each
   * subset {CEi,CEj} with i != j
   */
  @Override
  public void visit(final OWLDisjointClassesAxiom axiom) {
    final List<OWLClassExpression> classExpressions = axiom.getClassExpressionsAsList();
    LOG.debug("Converting DisjointClasses axiom: {}, {}", axiom, classExpressions);

    for (int i = 0; i < classExpressions.size(); i++) {
      for (int j = i + 1; j < classExpressions.size(); j++) {
        df.getOWLSubClassOfAxiom(//
            classExpressions.get(i), df.getOWLObjectComplementOf(classExpressions.get(j))//
        ).accept(this);
      }
    }
  }

  /**
   * DisjointUnion class expression allows one to define a class as a disjoint union of several
   * class expressions and thus to express covering constraints.<br>
   * <br>
   * Example:<br>
   * <br>
   * class expression: DisjointUnion(a:Child a:Boy a:Girl)<br>
   * <br>
   * converted: Each child is either a boy or a girl, each boy is a child, each girl is a child, and
   * nothing can be both a boy and a girl.<br>
   * <br>
   * <br>
   * We rewrite DisjointUnion(C , CE1 ... CEn) as EquivalentClasses(C ,ObjectUnionOf(CE1 ... CEn))
   * and DisjointClasses(CE1 ... CEn)
   */
  @Override
  public void visit(final OWLDisjointUnionAxiom axiom) {
    LOG.debug("Converting OWLDisjointUnionAxiom");

    final Set<OWLClassExpression> classExpressions = axiom.getClassExpressions();

    df.getOWLEquivalentClassesAxiom(//
        axiom.getOWLClass(), df.getOWLObjectUnionOf(classExpressions)//
    ).accept(this);

    df.getOWLDisjointClassesAxiom(classExpressions).accept(this);
  }

  // #########################################################
  // ################# object property axioms ################
  // #########################################################

  @Override
  public void visit(final OWLSubObjectPropertyOfAxiom axiom) {
    LOG.debug("Converting OWLSubObjectPropertyOf");

    addClause( //
        peConverter.asNLGElement(axiom.getSubProperty(), true), //
        Words.imply, //
        peConverter.asNLGElement(axiom.getSuperProperty())//
    ).getObject()// Do we need this?
        .setFeature(Feature.COMPLEMENTISER, null);
  }

  @Override
  public void visit(final OWLEquivalentObjectPropertiesAxiom axiom) {
    LOG.debug("Converting OWLEquivalentDataPropertiesAxiom");

    final NPPhraseSpec s = getSubject(axiom);
    final VPPhraseSpec v = Phrases.getBe(nlgFactory);
    final NPPhraseSpec o = nlgFactory.createNounPhrase(Words.synonym);
    o.setPlural(true);

    addClause(s, v, o).setPlural(true);
  }

  @Override
  public void visit(final OWLDisjointObjectPropertiesAxiom axiom) {
    LOG.debug("Converting OWLDisjointObjectPropertiesAxiom");

    final NPPhraseSpec s = getSubject(axiom);
    final VPPhraseSpec v = Phrases.getBe(nlgFactory);
    final NPPhraseSpec o = nlgFactory.createNounPhrase(Words.pairwiseDisjoint);
    o.setPlural(true);

    addClause(s, v, o).setPlural(true);
  }

  /**
   * SubClassOf(ObjectSomeValuesFrom(OPE owl:Thing) CE)
   */
  @Override
  public void visit(final OWLObjectPropertyDomainAxiom axiom) {
    LOG.debug("Converting OWLObjectPropertyDomainAxiom");

    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  @Override
  public void visit(final OWLObjectPropertyRangeAxiom axiom) {
    LOG.debug("Converting OWLObjectPropertyRangeAxiom");

    final String dataProperty = getPropertyVerbalizationText(//
        axiom.getProperty().asOWLObjectProperty()//
    );

    final NPPhraseSpec s = Phrases.getProperty(nlgFactory, Words.range, dataProperty, Words.object);
    final VPPhraseSpec v = Phrases.getBe(nlgFactory);
    final NPPhraseSpec o = nlgFactory//
        .createNounPhrase(axiom.getRange().accept(ceConverter.owlClassExpression));

    addClause(s, v, o);
  }

  /**
   */
  @Override
  public void visit(final OWLInverseObjectPropertiesAxiom axiom) {
    LOG.debug("Converting OWLInverseObjectPropertiesAxiom");

    final String forward = getPropertyVerbalizationText(axiom.getFirstProperty());
    final String inverse = getPropertyVerbalizationText(axiom.getSecondProperty());

    final NPPhraseSpec s = nlgFactory.createNounPhrase();
    final VPPhraseSpec v = Phrases.getBe(nlgFactory);
    final NPPhraseSpec o = nlgFactory.createNounPhrase();

    s.setDeterminer(Words.the);
    s.setPreModifier(inverse);
    s.setNoun(Words.property);

    final PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
    o.setPreModifier(Words.the);
    o.setNoun(Words.opposite);
    o.setPostModifier(pp);

    final NPPhraseSpec np = nlgFactory.createNounPhrase();
    pp.setPreModifier(Words.of);
    pp.setPreposition(np);

    np.setDeterminer(Words.the);
    np.setNoun(forward);
    np.setPostModifier(Words.property);

    addClause(s, v, o);
  }

  /**
   * SubClassOf(owl:Thing ObjectMaxCardinality(1 OPE))
   */
  @Override
  public void visit(final OWLFunctionalObjectPropertyAxiom axiom) {
    LOG.debug("Converting OWLFunctionalObjectPropertyAxiom");
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  @Override
  public void visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
    LOG.debug("Converting OWLAsymmetricObjectPropertyAxiom");

    final String propertyText =
        getPropertyVerbalizationText(axiom.getProperty().asOWLObjectProperty());

    final NPPhraseSpec s = nlgFactory.createNounPhrase();
    final VPPhraseSpec v = Phrases.getBe(nlgFactory);
    final NPPhraseSpec o = nlgFactory.createNounPhrase();

    { // If an individual
      s.setPreModifier(Words.iff);
      s.setNoun(Phrases.getAnIndividual(nlgFactory));
    }

    final VPPhraseSpec vp = nlgFactory.createVerbPhrase();
    {// with the X property
      final NPPhraseSpec np = Phrases.getProperty(nlgFactory, propertyText, Words.object);
      final PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
      pp.setPreposition(Words.with);
      pp.setPostModifier(np);
      vp.setPostModifier(pp);
    }

    { // to another individual
      final PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
      final NPPhraseSpec np = nlgFactory.createNounPhrase();
      pp.setPreposition(Words.to);
      pp.setPostModifier(np);
      np.setDeterminer(Words.another);
      np.setNoun(Words.individual);

      vp.addPostModifier(pp);
    }
    vp.setVerb(Words.connect);
    vp.setFeature(Feature.TENSE, Tense.PAST);

    o.setNoun(vp);

    // then this other individual is not connected with the X property to the individual
    final AdvPhraseSpec then = nlgFactory.createAdverbPhrase();
    {
      then.setAdverb(Words.then);
      o.setComplement(then);
    }
    final NPPhraseSpec in = Phrases.getIndividual(nlgFactory);
    { // this other individual
      in.setDeterminer(Words.thiss);
      in.setPreModifier(Words.other);
      then.setPostModifier(in);
    }
    final VPPhraseSpec notConnect = nlgFactory.createVerbPhrase(Words.connect);
    { // is not connected with the X property
      notConnect.setFeature(Feature.TENSE, Tense.PAST);
      final VPPhraseSpec is = Phrases.getBe(nlgFactory);
      is.setFeature(Feature.NEGATED, true);
      is.setPostModifier(notConnect);
      in.setPostModifier(is);

      // with the X property
      final PPPhraseSpec with = nlgFactory.createPrepositionPhrase();
      final NPPhraseSpec np = Phrases.getProperty(nlgFactory, propertyText, Words.object);
      with.setPreposition(Words.with);
      with.setPostModifier(np);
      notConnect.setPostModifier(with);
    }

    { // to the individual
      final PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
      final NPPhraseSpec np = Phrases.getTheIndividual(nlgFactory);
      pp.setPreposition(Words.to);
      pp.setPostModifier(np);

      notConnect.addPostModifier(pp);
    }
    addClause(s, v, o);
  }

  /**
   * SubClassOf(owl:Thing ObjectHasSelf(OPE))
   */
  @Override
  public void visit(final OWLReflexiveObjectPropertyAxiom axiom) {
    LOG.debug("Converting OWLReflexiveObjectPropertyAxiom");
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  /**
   * SubObjectPropertyOf(OPE ObjectInverseOf(OPE))
   */
  @Override
  public void visit(final OWLSymmetricObjectPropertyAxiom axiom) {
    LOG.debug("Converting OWLSymmetricObjectPropertyAxiom");
    df.getOWLSubObjectPropertyOfAxiom(//
        axiom.getProperty(), //
        axiom.getProperty().getInverseProperty()//
    ).accept(this);
  }

  // SubObjectPropertyOf(ObjectPropertyChain(OPE OPE) OPE)
  @Override
  public void visit(final OWLTransitiveObjectPropertyAxiom axiom) {
    LOG.debug("Converting OWLTransitiveObjectPropertyAxiom");

    final List<OWLObjectPropertyExpression> chain = new ArrayList<>();
    chain.add(axiom.getProperty());

    df.getOWLSubPropertyChainOfAxiom(chain, axiom.getProperty()).accept(this);
  }

  /**
   * SubClassOf(ObjectHasSelf(OPE) owl:Nothing)
   */
  @Override
  public void visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
    LOG.debug("Converting OWLIrreflexiveObjectPropertyAxiom");
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  /**
   * SubClassOf(owl:Thing ObjectMaxCardinality(1 ObjectInverseOf(OPE)))
   */
  @Override
  public void visit(final OWLInverseFunctionalObjectPropertyAxiom axiom) {
    LOG.debug("Converting OWLInverseFunctionalObjectPropertyAxiom");
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  // #########################################################
  // ################# data property axioms ##################
  // #########################################################

  /**
   * A data subproperty axiom SubDataPropertyOf(DPE1 DPE2) states that the data property expression
   * DPE1 is a subproperty of the data property expression DPE2 — that is, if an individual x is
   * connected by DPE1 to a literal y, then x is connected by DPE2 to y as well. Example:<br>
   * <br>
   * SubDataPropertyOf(a:hasLastName a:hasName) <br>
   * A last name of someone is his/her name as well.
   */
  @Override
  public void visit(final OWLSubDataPropertyOfAxiom axiom) {
    LOG.debug("Converting OWLSubDataPropertyOfAxiom");

    final OWLDataPropertyExpression subProperty = axiom.getSubProperty();
    final OWLDataPropertyExpression superProperty = axiom.getSuperProperty();

    final NLGElement subPropertyElement = peConverter.asNLGElement(subProperty, true); // why true?
    final NLGElement superPropertyElement = peConverter.asNLGElement(superProperty);

    superPropertyElement.setFeature(Feature.COMPLEMENTISER, null);

    addClause(subPropertyElement, Phrases.getBe(nlgFactory), superPropertyElement);
  }

  /**
   */
  @Override
  public void visit(final OWLEquivalentDataPropertiesAxiom axiom) {
    LOG.debug("Converting OWLEquivalentDataPropertiesAxiom");

    final NPPhraseSpec s = getSubject(axiom);
    final VPPhraseSpec v = Phrases.getBe(nlgFactory);
    final NPPhraseSpec o = nlgFactory.createNounPhrase(Words.synonym);

    s.setPlural(true);
    o.setPlural(true);

    addClause(s, v, o);
  }

  /**
   * A disjoint data properties axiom DisjointDataProperties(DPE1 ... DPEn) states that all of the
   * data property expressions DPEi, 1 ≤ i ≤ n, are pairwise disjoint; that is, no individual x can
   * be connected to a literal y by both DPEi and DPEj for i ≠ j.<br>
   * <br>
   * Example:<br>
   * DisjointDataProperties(a:hasName a:hasAddress) <br>
   * Someone's name must be different from his address.
   */
  @Override
  public void visit(final OWLDisjointDataPropertiesAxiom axiom) {
    LOG.debug("Converting OWLDisjointDataPropertiesAxiom");

    final NPPhraseSpec s = getSubject(axiom);
    final VPPhraseSpec v = Phrases.getBe(nlgFactory);
    final NPPhraseSpec o = nlgFactory.createNounPhrase(Words.pairwiseDisjoint);
    o.setPlural(true);

    addClause(s, v, o).setPlural(true);
  }

  /**
   * A data property domain axiom DataPropertyDomain( DPE CE ) states that the domain of the data
   * property expression DPE is the class expression CE
   *
   *
   * example<br>
   * Only people can have names. <br>
   * DataPropertyDomain( a:hasName a:Person )
   */
  @Override
  public void visit(final OWLDataPropertyDomainAxiom axiom) {
    LOG.debug("Converting OWLDataPropertyDomainAxiom");

    final NPPhraseSpec s = nlgFactory.createNounPhrase();
    final VPPhraseSpec v = Phrases.getBe(nlgFactory);
    final NLGElement o = axiom.getDomain().accept(ceConverter.owlClassExpression);

    s.setDeterminer(Words.the);
    s.setNoun(//
        Words.domain.concat(" ")//
            .concat(Words.of).concat(" ")//
            .concat(Words.the).concat(" ")//
            .concat(getPropertyVerbalizationText(axiom.getProperty().asOWLDataProperty()))
            .concat(" ")//
            .concat(Words.property));

    addClause(s, v, o);
  }

  /**
   * example<br>
   * The range of the a:hasName property is xsd:string.<br>
   * DataPropertyRange( a:hasName xsd:string )
   */
  @Override
  public void visit(final OWLDataPropertyRangeAxiom axiom) {
    LOG.debug("Converting OWLDataPropertyRangeAxiom");

    final OWLDataRange range = axiom.getRange();

    final String verbalizationText = getPropertyVerbalizationText(//
        axiom.getProperty().asOWLDataProperty()//
    );

    final NPPhraseSpec s = nlgFactory.createNounPhrase();
    final VPPhraseSpec v = Phrases.getBe(nlgFactory);
    final NPPhraseSpec o = nlgFactory.createNounPhrase(range.accept(ceConverter.owlDataRange));

    s.setDeterminer(Words.the);
    s.setNoun(//
        Words.range.concat(" ")//
            .concat(Words.of).concat(" ")//
            .concat(Words.the).concat(" ")//
            .concat(verbalizationText).concat(" ")//
            .concat(Words.property));

    addClause(s, v, o);
  }

  @Override
  public void visit(final OWLFunctionalDataPropertyAxiom axiom) {
    LOG.debug("Converting OWLFunctionalDataPropertyAxiom");

    final String verbalizationText = getPropertyVerbalizationText(axiom.getProperty());

    final NPPhraseSpec s = Phrases.getAnIndividual(nlgFactory);

    final VPPhraseSpec v = nlgFactory.createVerbPhrase();
    v.setPreModifier(Words.can);
    v.setVerb(Words.have);

    final AdvPhraseSpec a = nlgFactory.createAdverbPhrase();
    {
      a.setPreModifier(Words.at);
      a.setAdverb(Words.much);
      a.setPostModifier(Words.one);
      a.setFeature(Feature.IS_SUPERLATIVE, true);
    }
    final NPPhraseSpec value = nlgFactory.createNounPhrase();
    {
      value.setNoun(Words.value);
      value.setPreModifier(a);
    }
    final PPPhraseSpec forr = nlgFactory.createPrepositionPhrase();
    {
      forr.setPreposition(verbalizationText);
      forr.setPreModifier(Words.forr);
    }
    final NPPhraseSpec o = nlgFactory.createNounPhrase();
    {
      o.setNoun(value);
      o.setPostModifier(forr);
    }

    addClause(s, v, o);
  }

  // #########################################################
  // ################# other logical axioms ##################
  // #########################################################

  /**
   * The more complex form is SubObjectPropertyOf( ObjectPropertyChain( OPE1 ... OPEn ) OPE ). This
   * axiom states that, if an individual x is connected by a sequence of object property expressions
   * OPE1, ..., OPEn with an individual y, then x is also connected with y by the object property
   * expression OPE. <br>
   * Such axioms are also known as complex role inclusions [SROIQ].
   */
  // ORDER!
  @Override
  public void visit(final OWLSubPropertyChainOfAxiom axiom) {
    final NPPhraseSpec s = nlgFactory.createNounPhrase();
    final VPPhraseSpec v = nlgFactory.createVerbPhrase();
    final NPPhraseSpec o = nlgFactory.createNounPhrase();

    final String superProperty = getPropertyVerbalizationText(axiom.getSuperProperty());
    // If an individual is connected by the sequence of the X,Y, and Z object property with another
    // individual, then the individual is also connected by the A object property with the another
    // individual.
    {
      // if an individual
      s.setPreModifier(Words.iff);
      s.setNoun(Phrases.getAnIndividual(nlgFactory));
    }

    {
      // is connected
      final VPPhraseSpec be = nlgFactory.createVerbPhrase(Words.be);
      be.setFeature(Feature.PERSON, Person.THIRD);
      v.setPreModifier(be);
      v.setVerb(Words.connect);
      v.setFeature(Feature.TENSE, Tense.PAST);
    }

    // with another individual
    final NPPhraseSpec eee = Phrases.getIndividual(nlgFactory);
    eee.setDeterminer(Words.with);
    eee.setPreModifier(Words.another);
    v.setPostModifier(eee);

    // by
    final PPPhraseSpec by = nlgFactory.createPrepositionPhrase();
    o.setNoun(by);
    by.setPreModifier(Words.by);

    final NPPhraseSpec theListofProperties = nlgFactory.createNounPhrase();
    theListofProperties.setDeterminer(Words.the);

    if (axiom.getPropertyChain().size() > 1) {
      // the sequence
      final NPPhraseSpec theseq = nlgFactory.createNounPhrase();
      by.setPreposition(theseq);

      theseq.setDeterminer(Words.the);
      theseq.setNoun(Words.sequence);
      // of
      final PPPhraseSpec of = nlgFactory.createPrepositionPhrase();
      theseq.setPostModifier(of);

      of.setPreModifier(Words.of);
      of.setPreposition(theListofProperties);
    } else {
      by.setPostModifier(theListofProperties);
    }

    // list
    final CoordinatedPhraseElement coo = nlgFactory.createCoordinatedPhrase();
    coo.setConjunction(Words.and);
    for (final OWLObjectPropertyExpression p : axiom.getPropertyChain()) {
      // TODO: check type?
      coo.addCoordinate(nlgFactory.createStringElement(getPropertyVerbalizationText(p)));
    }
    o.setPostModifier(coo);

    // object property
    final NPPhraseSpec op = nlgFactory.createNounPhrase();
    op.setNoun(Words.object.concat(" ").concat(Words.property));

    //
    coo.addPostModifier(op);

    final NPPhraseSpec oo = nlgFactory.createNounPhrase();

    // then this other individual is not connected with the X property to the individual
    final AdvPhraseSpec then = nlgFactory.createAdverbPhrase();
    {
      then.setAdverb(Words.then);
      oo.setComplement(then);
    }
    final NPPhraseSpec in = Phrases.getIndividual(nlgFactory);
    { // this other individual
      in.setDeterminer(Words.the);
      // in.setPreModifier(Words.other);
      then.setPostModifier(in);
    }
    final VPPhraseSpec connect = nlgFactory.createVerbPhrase(Words.connect);
    { // is also connected by the X property
      connect.setFeature(Feature.TENSE, Tense.PAST);
      final VPPhraseSpec is = Phrases.getBe(nlgFactory);
      is.setPostModifier(connect);
      connect.setPreModifier(Words.also);
      in.setPostModifier(is);

      // by the X property
      final PPPhraseSpec with = nlgFactory.createPrepositionPhrase();
      final NPPhraseSpec np = Phrases.getProperty(nlgFactory, superProperty, Words.object);
      with.setPreposition(Words.by);
      with.setPostModifier(np);
      connect.setPostModifier(with);
    }

    { // with the other individual
      final PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
      final NPPhraseSpec np = Phrases.getTheIndividual(nlgFactory);
      np.setPreModifier(Words.other);
      pp.setPreposition(Words.with);
      pp.setPostModifier(np);

      connect.addPostModifier(pp);
    }

    // eee.addComplement(oo);
    v.addPostModifier(oo);
    addClause(s, v, o);
  }

  // TODO:
  @Override
  public void visit(final OWLHasKeyAxiom axiom) {
    LOG.debug("Converting OWLHasKeyAxiom");

    final OWLClassExpression ce = axiom.getClassExpression();
    axiom.getDataPropertyExpressions();
    axiom.getObjectPropertyExpressions();

    final NLGElement s = ceConverter.asNLGElement(ce, true);
    final VPPhraseSpec v = nlgFactory.createVerbPhrase();
    final NPPhraseSpec o = nlgFactory.createNounPhrase();

    {
      // is connected
      final VPPhraseSpec be = nlgFactory.createVerbPhrase(Words.be);
      be.setFeature(Feature.PERSON, Person.THIRD);
      be.addPostModifier(nlgFactory.createWord(Words.uniquely, LexicalCategory.ADVERB));
      v.setPreModifier(be);
      v.setVerb(Words.identify);
      v.setFeature(Feature.TENSE, Tense.PAST);
    }

    final CoordinatedPhraseElement coo = nlgFactory.createCoordinatedPhrase();
    coo.setConjunction(Words.and);

    for (final OWLPropertyExpression pe : axiom.getPropertyExpressions()) {
      coo.addCoordinate(nlgFactory.createStringElement(getPropertyVerbalizationText(pe)));
    }

    // o.setPreModifier(Words.by);
    // o.addPreModifier(Words.its);
    o.addPreModifier(Words.by + " " + Words.its);

    o.setComplement(coo);

    addClause(s, v, o);
  }

  @Override
  public void visit(final OWLDatatypeDefinitionAxiom axiom) {
    LOG.debug("Converting OWLDatatypeDefinitionAxiom");

    final OWLDatatype datatype = axiom.getDatatype();
    final OWLDataRange datarange = axiom.getDataRange();

    final NPPhraseSpec s = nlgFactory.createNounPhrase(Words.datatype);
    final VPPhraseSpec v = Phrases.getBe(nlgFactory);
    final NPPhraseSpec o = nlgFactory.createNounPhrase(datarange.accept(ceConverter.owlDataRange));

    s.setPreModifier(Words.the);

    if (datatype.isNamed()) {
      s.setPostModifier(datatype.getIRI().getShortForm());
    } else if (datatype.isBuiltIn()) {
      s.setPostModifier(datatype.getBuiltInDatatype().getShortForm());
    } else {
      LOG.warn("Not implemented yet.");
    }

    addClause(s, v, o);
  }

  @Override
  public void visit(final SWRLRule axiom) {
    LOG.debug("Converting SWRLRule");
    /**
     * not explicit in the OWL 2 specification<br>
     * <code>

    final Set<SWRLAtom> concequent = axiom.getHead();
    final Set<SWRLAtom> antecedent = axiom.getBody();

    LOG.info("type {} ", axiom.getAxiomType());

    LOG.info("type {} ", axiom.getSimplified());

    LOG.info("head:{}\nbody:{}", concequent, antecedent);
    </code>
     */
  }

  // #########################################################
  // ################# individual axioms #####################
  // #########################################################

  @Override
  public void visit(final OWLClassAssertionAxiom axiom) {
    LOG.debug("OWLClassAssertionAxiom");
  }

  @Override
  public void visit(final OWLObjectPropertyAssertionAxiom axiom) {
    LOG.debug("OWLObjectPropertyAssertionAxiom ");
  }

  @Override
  public void visit(final OWLDataPropertyAssertionAxiom axiom) {
    LOG.debug("OWLDataPropertyAssertionAxiom");
  }

  @Override
  public void visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {
    LOG.debug("OWLNegativeObjectPropertyAssertionAxiom");
  }

  @Override
  public void visit(final OWLNegativeDataPropertyAssertionAxiom axiom) {
    LOG.debug("OWLNegativeDataPropertyAssertionAxiom");
  }

  @Override
  public void visit(final OWLDifferentIndividualsAxiom axiom) {
    LOG.debug("OWLDifferentIndividualsAxiom");
  }

  @Override
  public void visit(final OWLSameIndividualAxiom axiom) {
    LOG.debug("OWLSameIndividualAxiom");
  }

  // #########################################################
  // ################# non-logical axioms ####################
  // #########################################################

  @Override
  public void visit(final OWLAnnotationAssertionAxiom axiom) {
    LOG.debug("OWLAnnotationAssertionAxiom");
  }

  @Override
  public void visit(final OWLSubAnnotationPropertyOfAxiom axiom) {
    LOG.debug("OWLSubAnnotationPropertyOfAxiom");
  }

  @Override
  public void visit(final OWLAnnotationPropertyDomainAxiom axiom) {
    LOG.debug("OWLAnnotationPropertyDomainAxiom");
  }

  @Override
  public void visit(final OWLAnnotationPropertyRangeAxiom axiom) {
    LOG.debug("OWLAnnotationPropertyRangeAxiom");
  }

  @Override
  public void visit(final OWLDeclarationAxiom axiom) {
    LOG.debug("OWLDeclarationAxiom");
  }

  // #########################################################
  // ################# helpers ###############################
  // #########################################################
  private NPPhraseSpec getSubject(final OWLPropertyAxiom axiom) {

    final CoordinatedPhraseElement coo = nlgFactory.createCoordinatedPhrase();
    coo.setConjunction(Words.and);

    final NPPhraseSpec np = nlgFactory.createNounPhrase();
    final NPPhraseSpec npp = nlgFactory.createNounPhrase();

    int n = 0;
    if (axiom instanceof OWLObjectPropertyAxiom) {
      Set<OWLObjectPropertyExpression> e;
      if (axiom instanceof OWLDisjointObjectPropertiesAxiom) {
        e = ((OWLDisjointObjectPropertiesAxiom) axiom).getProperties();
      } else if (axiom instanceof OWLEquivalentObjectPropertiesAxiom) {
        e = ((OWLEquivalentObjectPropertiesAxiom) axiom).getProperties();
        // } else if (axiom instanceof OWLSubPropertyChainOfAxiom) {
        // e = ((OWLSubPropertyChainOfAxiom) axiom).getPropertyChain();
      } else {
        LOG.warn("Could not process: {}", axiom.getAxiomType());
        e = new HashSet<>();
      }
      n = e.size();
      for (final OWLObjectPropertyExpression p : e) {
        // TODO: check type?
        coo.addCoordinate(nlgFactory.createStringElement(getPropertyVerbalizationText(p)));
      }
      npp.setPreModifier(Words.object);
    } else if (axiom instanceof OWLDataPropertyAxiom) {
      Set<OWLDataPropertyExpression> e;
      if (axiom instanceof OWLDisjointDataPropertiesAxiom) {
        e = ((OWLDisjointDataPropertiesAxiom) axiom).getProperties();
      } else if (axiom instanceof OWLEquivalentDataPropertiesAxiom) {
        e = ((OWLEquivalentDataPropertiesAxiom) axiom).getProperties();
      } else {
        e = new HashSet<>();
      }

      n = e.size();

      for (final OWLDataPropertyExpression p : e) {
        // TODO: check type?
        coo.addCoordinate(nlgFactory.createStringElement(getPropertyVerbalizationText(p)));
      }
      npp.setPreModifier(Words.data);
    } else {
      LOG.debug("Should never be the case.");
    }

    // data or object property
    npp.setNoun(Words.property);
    npp.setPlural(true);

    np.setDeterminer(Words.the);
    np.setPreModifier(Words.number(n));
    np.setNoun(npp);

    final NPPhraseSpec s = nlgFactory.createNounPhrase();
    s.setPreModifier(np);
    s.setNoun(coo);

    return s;
  }

  /**
   * Adds an instance of SPhraseSpec to {@link #clauses}.
   *
   * @param s subject
   * @param v verb
   * @param o object
   *
   * @return added clause or null
   */
  private SPhraseSpec addClause(final Object s, final Object v, final Object o) {
    final SPhraseSpec c = Phrases.createClause(nlgFactory, s, v, o);
    return clauses.add(c) ? c : null;
  }

  private PropertyVerbalization getPropertyVerbalization(final OWLPropertyExpression p) {
    return ((OWLClassExpressionToNLGElement) ceConverter.owlClassExpression).propertyVerbalizer(p);
  }

  private String getPropertyVerbalizationText(final OWLPropertyExpression p) {
    return getPropertyVerbalization(p).getVerbalizationText();
  }
}
