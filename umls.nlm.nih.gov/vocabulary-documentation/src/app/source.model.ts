export interface SourceModel {
    classType: string;
    abbreviation: string;
    expandedForm: string;
    family?: string;
    language?: {
      classType: string;
      abbreviation: string;
      expandedForm: string;
    };
    restrictionLevel?: number;
    acquisitionContact?: string;
    contentContact?: {
      classType: string;
      handle: string;
      name: string;
      title: string;
      organization: string;
      address1: string;
      address2: string;
      city: string;
      stateOrProvince: string;
      country: string;
      zipCode: string;
      telephone: string;
      fax: string;
      email: string;
      url: string;
      value: string;
    };
    // Add other fields as necessary
  }